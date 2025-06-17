package org.example.gimnasio.hardware;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Clase para manejar la comunicación serial con Arduino y lector de huellas R307S
 */
public class FingerprintReader {

    private Process arduinoProcess;
    private BufferedWriter writer;
    private BufferedReader reader;
    private boolean isConnected = false;
    private static final String ARDUINO_PORT = "COM4"; // Cambiar según tu puerto
    private static final int TIMEOUT_SECONDS = 30;

    public FingerprintReader() {
        connectToArduino();
    }

    private void connectToArduino() {
        try {
            // En Windows, usar mode para configurar el puerto serial
            ProcessBuilder modeBuilder = new ProcessBuilder("mode", ARDUINO_PORT + ":", "BAUD=9600", "PARITY=N", "DATA=8", "STOP=1");
            modeBuilder.start().waitFor();

            // Conectar al puerto serial usando Python helper script o directamente
            // Para simplificar, usaremos un enfoque básico
            ProcessBuilder pb = new ProcessBuilder("python", "-c",
                    "import serial; import sys; " +
                            "ser = serial.Serial('" + ARDUINO_PORT + "', 9600, timeout=1); " +
                            "while True: " +
                            "  line = input(); " +
                            "  if line == 'EXIT': break; " +
                            "  ser.write((line + '\\n').encode()); " +
                            "  response = ser.readline().decode().strip(); " +
                            "  print(response, flush=True)"
            );

            arduinoProcess = pb.start();
            writer = new BufferedWriter(new OutputStreamWriter(arduinoProcess.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(arduinoProcess.getInputStream()));

            // Verificar conexión
            String response = sendCommand("STATUS");
            if (response != null && response.contains("STATUS_START")) {
                isConnected = true;
                System.out.println("Arduino conectado exitosamente");
            }

        } catch (Exception e) {
            System.err.println("Error conectando con Arduino: " + e.getMessage());
            isConnected = false;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    private String sendCommand(String command) {
        if (!isConnected) {
            return null;
        }

        try {
            writer.write(command + "\n");
            writer.flush();

            // Leer respuesta con timeout
            String response = reader.readLine();
            return response;

        } catch (IOException e) {
            System.err.println("Error enviando comando: " + e.getMessage());
            return null;
        }
    }

    private List<String> sendCommandMultiResponse(String command, String endMarker) {
        List<String> responses = new ArrayList<>();

        if (!isConnected) {
            return responses;
        }

        try {
            writer.write(command + "\n");
            writer.flush();

            String line;
            long startTime = System.currentTimeMillis();

            while ((line = reader.readLine()) != null) {
                responses.add(line);

                if (line.equals(endMarker) || line.contains("ERROR")) {
                    break;
                }

                // Timeout de seguridad
                if (System.currentTimeMillis() - startTime > TIMEOUT_SECONDS * 1000) {
                    responses.add("TIMEOUT");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error en comando multi-respuesta: " + e.getMessage());
        }

        return responses;
    }

    /**
     * Registra una nueva huella digital
     */
    public FingerprintResult enrollFingerprint(int fingerprintId) {
        List<String> responses = sendCommandMultiResponse("ENROLL:" + fingerprintId, "ENROLL_SUCCESS");

        FingerprintResult result = new FingerprintResult();
        result.setId(fingerprintId);

        for (String response : responses) {
            if (response.contains("ENROLL_SUCCESS")) {
                result.setSuccess(true);
                result.setMessage("Huella registrada exitosamente");
                break;
            } else if (response.contains("ERROR")) {
                result.setSuccess(false);
                result.setMessage("Error al registrar huella: " + response);
                break;
            } else if (response.equals("PLACE_FINGER")) {
                result.setMessage("Coloque el dedo en el sensor");
            } else if (response.equals("REMOVE_FINGER")) {
                result.setMessage("Retire el dedo del sensor");
            } else if (response.equals("PLACE_SAME_FINGER")) {
                result.setMessage("Coloque el mismo dedo nuevamente");
            }
        }

        return result;
    }

    /**
     * Verifica una huella digital
     */
    public FingerprintResult verifyFingerprint() {
        String response = sendCommand("VERIFY");

        FingerprintResult result = new FingerprintResult();

        if (response != null && response.startsWith("VERIFY_MATCH")) {
            String[] parts = response.split(":");
            if (parts.length >= 3) {
                result.setSuccess(true);
                result.setId(Integer.parseInt(parts[1]));
                result.setConfidence(Integer.parseInt(parts[2]));
                result.setMessage("Huella verificada exitosamente");
            }
        } else if (response != null && response.equals("VERIFY_NO_MATCH")) {
            result.setSuccess(false);
            result.setMessage("Huella no encontrada");
        } else if (response != null && response.equals("VERIFY_NO_FINGER")) {
            result.setSuccess(false);
            result.setMessage("No se detectó dedo en el sensor");
        } else {
            result.setSuccess(false);
            result.setMessage("Error al verificar huella");
        }

        return result;
    }

    /**
     * Elimina una huella específica
     */
    public boolean deleteFingerprint(int fingerprintId) {
        String response = sendCommand("DELETE:" + fingerprintId);
        return response != null && response.contains("DELETE_SUCCESS");
    }

    /**
     * Elimina todas las huellas
     */
    public boolean deleteAllFingerprints() {
        String response = sendCommand("DELETE_ALL");
        return response != null && response.contains("DELETE_ALL_SUCCESS");
    }

    /**
     * Obtiene el estado del sensor
     */
    public int getTemplateCount() {
        List<String> responses = sendCommandMultiResponse("STATUS", "STATUS_END");

        for (String response : responses) {
            if (response.startsWith("TEMPLATE_COUNT:")) {
                return Integer.parseInt(response.split(":")[1]);
            }
        }

        return -1;
    }

    /**
     * Cierra la conexión con Arduino
     */
    public void disconnect() {
        try {
            if (writer != null) {
                writer.write("EXIT\n");
                writer.flush();
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (arduinoProcess != null) {
                arduinoProcess.destroyForcibly();
            }
            isConnected = false;
        } catch (IOException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }

    /**
     * Clase para encapsular resultados de operaciones con huella
     */
    public static class FingerprintResult {
        private boolean success;
        private int id;
        private int confidence;
        private String message;
        private String fingerprintHash;

        // Getters y setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getConfidence() { return confidence; }
        public void setConfidence(int confidence) { this.confidence = confidence; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getFingerprintHash() { return fingerprintHash; }
        public void setFingerprintHash(String fingerprintHash) { this.fingerprintHash = fingerprintHash; }
    }
}