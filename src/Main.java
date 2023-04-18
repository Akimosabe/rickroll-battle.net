import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {
        // Создаем объект класса Desktop
        Desktop desktop = Desktop.getDesktop();
        // Создаем переменную url с ссылкой на видео
        String url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

        while (true) {
            // Проверяем, запущена ли программа Battle.net
            boolean isRunning = isProcessRunning("Battle.net.exe");

            // Если программа запущена более 20 минут, открываем ссылку в браузере по умолчанию
            if (isRunning) {
                Instant startTime = getProcessStartTime("Battle.net.exe");
                Duration elapsedTime = Duration.between(startTime, Instant.now());
                if (elapsedTime.toMinutes() > 20) {
                    try {
                        desktop.browse(new URI(url)); // Открываем ссылку в браузере по умолчанию
                    } catch (IOException | URISyntaxException e) {
                        System.err.println("Не удалось открыть ссылку: " + e.getMessage());
                    }
                }
            }

            // Пауза на 2 минуты
            try {
                Thread.sleep(2 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод для проверки, запущен ли процесс с заданным именем
     */
    private static boolean isProcessRunning(String processName) {
        try {
            // Создаем объект ProcessBuilder с заданным именем процесса
            ProcessBuilder processBuilder = new ProcessBuilder("tasklist.exe", "/fi", "imagename eq " + processName);
            // Запускаем процесс
            Process process = processBuilder.start();
            // Считываем вывод процесса
            String output = new String(process.getInputStream().readAllBytes());
            // Проверяем, содержит ли вывод имя процесса
            return output.contains(processName);
        } catch (IOException e) {
            System.err.println("Не удалось проверить, запущен ли процесс: " + e.getMessage());
            return false;
        }
    }
    /**
     * Метод для получения времени запуска процесса в миллисекундах
     */
    private static Instant getProcessStartTime(String processName) {
        try {
            // Создаем объект ProcessBuilder с заданным именем процесса и параметром "/fo csv"
            ProcessBuilder processBuilder = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/fi", "imagename eq " + processName);
            // Запускаем процесс
            Process process = processBuilder.start();
            // Считываем вывод процесса
            String output = new String(process.getInputStream().readAllBytes());
            // Ищем строку, начинающуюся с имени процесса и содержащую информацию о времени запуска
            int index = output.indexOf(processName);
            if (index >= 0) {
                String[] parts = output.substring(index).split(",");
                if (parts.length >= 4) {
// Получаем строку с временем запуска
                    String timeString = parts[3].trim();
// Преобразуем строку в long, представляющий время в миллисекундах
                    long timeMs = parseTimeString(timeString);
// Возвращаем Instant, соответствующий времени запуска процесса
                    return Instant.ofEpochMilli(timeMs);
                } else {
                    throw new IOException("Неверный формат вывода: " + output);
                }
            } else {
                throw new IOException("Процесс не найден: " + processName);
            }
        } catch (IOException e) {
            System.err.println("Не удалось получить время запуска процесса: " + e.getMessage());
            return Instant.now();
        }
    }
    /**
     * Метод для преобразования строки времени запуска в long, представляющий время в миллисекундах
     */
    private static long parseTimeString(String timeString) {
        String[] parts = timeString.trim().split(":");
        if (parts.length == 3) {
            long hours = Long.parseLong(parts[0]);
            long minutes = Long.parseLong(parts[1]);
            long seconds = Long.parseLong(parts[2]);
            return hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000;
        }
        return -1;
    }
}