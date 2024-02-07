import java.util.concurrent.*;
import java.util.Random;

public class TextAnalyzer {
    private static final ArrayBlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static final ArrayBlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static final ArrayBlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    private static final String LETTERS = "abc";
    private static final int TEXT_LENGTH = 100_000;
    private static final int TEXT_COUNT = 10_000;

    public static void main(String[] args) {
        new Thread(() -> {
            for (int i = 0; i < TEXT_COUNT; i++) {
                String text = generateText(LETTERS, TEXT_LENGTH);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> analyzeQueue(queueA, 'a')).start();
        new Thread(() -> analyzeQueue(queueB, 'b')).start();
        new Thread(() -> analyzeQueue(queueC, 'c')).start();
    }

    private static void analyzeQueue(ArrayBlockingQueue<String> queue, char letter) {
        int maxCount = 0;
        String maxText = null;

        try {
            for (int i = 0; i < TEXT_COUNT; i++) {
                String text = queue.take();
                int count = countLetter(text, letter);
                if (count > maxCount) {
                    maxCount = count;
                    maxText = text;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Максимальное количество '" + letter + "' в тексте: " + maxCount);
        //  System.out.println("Текст: " + maxText);
    }

    private static int countLetter(String text, char letter) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == letter) {
                count++;
            }
        }
        return count;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}