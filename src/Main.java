import java.util.*;

public class Main {
    // Таблица римских чисел в парах с арабскими значениями до 100
    private static Map<String, Integer> romanAlph = Map.ofEntries(
            Map.entry("I", 1),
            Map.entry("IV", 4),
            Map.entry("V", 5),
            Map.entry("IX", 9),
            Map.entry("X", 10),
            Map.entry("XL", 40),
            Map.entry("L", 50),
            Map.entry("XC", 90),
            Map.entry("C", 100)
    );

    /*
     * Преобразует строку с римским числом в арабское
     */
    private static int toArabic(String rn) throws Exception {
        if(rn.length() == 0)
            throw new Exception("Empty Roman Number");

        // Здесь будет накапливаться итоговый результат
        int res = 0;

        // Проходим по каждому символу в строке с арабским числом
        for(byte i = 0; i < rn.length(); i++) {
            // Сравниваем текущий и последующий символы, если значение текущего меньше последующего, то вычитаем значение текущей римской цифры, иначе прибавляем значение текущего символа
            if(i < rn.length() - 1 && romanAlph.get(String.valueOf(rn.charAt(i))) < romanAlph.get(String.valueOf(rn.charAt(i + 1))))
                res -= romanAlph.get("" + rn.charAt(i));
            else
                res += romanAlph.get("" + rn.charAt(i));
        }

        return res;
    }

    /*
     * Преобразует арабское целое число в римское в виде строки
     */
    private static String toRoman(int n) throws Exception {
        if(n <= 0)
            throw new Exception("Cannot be converted to a Roman number");

        String res = ""; // Строка, в которой будет накапливаться результат
        String oldKey = ""; // Запоминаем предыдущую (меньшего разряда) римскую цифру
        List<Map.Entry<String, Integer>> orderedRomanAlph = new ArrayList<>(romanAlph.entrySet()); // Преобразуем римско-арабские сопоставления в виде Map в List
        // Сортируем римско-арабские сопоставления, которые уже находятся в виде ArrayList
        Collections.sort(orderedRomanAlph, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                return e1.getValue() - e2.getValue();
            }
        });

        // Выполняем пока всё число не преобразуем в римское
        while(n > 0) {
            // Проходимся по отсортированным сопоставлениям
            for(Map.Entry<String, Integer> entry : orderedRomanAlph) {
                // Находим такое число в сопоставлениях, которое будет больше текущего числа, добавляем более мелкую римскую цифру в результирующую строку
                // И вычитаем значение римской цифры из текущего числа
                if(n < entry.getValue()) {
                    res += oldKey;
                    n -= romanAlph.get(oldKey);
                    break;
                }
                // Находим число в сопоставлениях, которое будет равно текущему, добавляем римскую цифру в результирующую строку
                // И вычитаем из текущего числа значение римской цифры (обнуляем таким образом)
                else if(n == entry.getValue()) {
                    res += entry.getKey();
                    n -= entry.getValue();
                    break;
                }
                oldKey = entry.getKey(); // Сохраняем текущую римскую цифру во временную переменную
            }
        }

        return res;
    }

    /*
     * Удаляет пробелы и табуляции в строке
     */
    private static String deleteSpaces(String s) {
        s = s.replaceAll(" ", "");
        s = s.replaceAll("\t", "");
        return s;
    }

    /*
     * Проверяем римское число
     */
    private static boolean checkNumber(String rn) throws Exception {
        int n = toArabic(rn);
        return checkNumber(n);
    }

    // Проверяем арабское число
    private static boolean checkNumber(int n) throws Exception {
        return n >= 1 && n <= 10;
    }

    /*
     * Проверяет, является ли число римским
     */
    private static boolean isRoman(String snum) throws Exception {
        try {
            toArabic(snum);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    /*
    * Проверяем формат строки и числа, содержащиеся в ней.
    * Упаковываем проверенные данные в массив строк из 4 элементов, где
    * [0]: "0" - Арабские числа, "1" - Римские числа
    * [1]: Операция ("+", "-", "*", "/")
    * [2]: Первое число (от "1" до "10")
    * [3]: Второе число (от "1" до "10")
    */
    private static String[] getData(String input) throws Exception {
        input = deleteSpaces(input); // Удаляем пробелы и табуляции из примера
        input = input.toUpperCase();
        String[] data = new String[4];
        String[] operations = {"+", "-", "*", "/"};

        // Проходимся по каждой возможной операции (+, -, *, /)
        for(String op : operations) {

            // Проверяем, есть ли текущая операция в примере
            if(input.contains(op)) {
                data[1] = op;
                String[] snums = input.split("[" + op + "]"); // Разбиваем пример на числа, при этом заэкранировав знак

                // Проверяем, чтобы в примере были только 2 числа
                if(snums.length == 2) {
                    data[0] = isRoman(snums[0]) ? "1" : "0"; // Проверяем первое число на систему счисления

                    // Проверяем второе число и сравниваем с системой счисления предыдущего
                    if(data[0] != (isRoman(snums[1]) ? "1" : "0"))
                        throw new Exception("Invalid Format");

                    // Проверяем, чтобы числа были от 1 до 10, и результат от римских чисел не получился меньше или равным нулю
                    if(data[0] == "0") {
                        if(!checkNumber(Integer.parseInt(snums[0])) || !checkNumber(Integer.parseInt(snums[1])))
                            throw new Exception("Invalid Number Format");
                    } else {
                        if(!checkNumber(snums[0]) || !checkNumber(snums[1]))
                            throw new Exception("Invalid Number Format");

                        if(toArabic(snums[0]) <= toArabic(snums[1]) && data[1].equals("-"))
                            throw new Exception("Invalid Result");
                    }

                    // Записываем числа в массив
                    data[2] = snums[0];
                    data[3] = snums[1];
                } else {
                    throw new Exception("Invalid Format");
                }

                return data;
            }
        }

        throw new Exception("Invalid Format");
    }

    /*
     * Выполняет сложение, вычитание, умножение и деление арабских чисел
     */
    private static String matchArabic(String op, int n1, int n2) throws Exception {
        int res = 0;

        switch(op) {
            case "+":
                res = n1 + n2;
                break;
            case "-":
                res = n1 - n2;
                break;
            case "*":
                res = n1 * n2;
                break;
            case "/":
                res = n1 / n2;
        }

        return String.valueOf(res);
    }

    /*
     * Выполняет сложение, вычитание, умножение и деление римских чисел
     */
    private static String matchRoman(String op, String rn1, String rn2) throws Exception {
        // Преобразуем римские числа в арабские
        int n1 = toArabic(rn1);
        int n2 = toArabic(rn2);
        int res = 0;

        // Выполняем необходимую операцию
        switch(op) {
            case "+":
                res = n1 + n2;
                break;
            case "-":
                res = n1 - n2;
                break;
            case "*":
                res = n1 * n2;
                break;
            case "/":
                res = n1 / n2;
        }

        // Если результат операции римских чисел меньше еденицы, бросаем ошибку
        if(res <= 0)
            throw new Exception("Invalid Result");

        return toRoman(res);
    }

    /*
     * Принимаем в качестве аргумента строку с примером (3 * 2 или || + V)
     * В примере могут присутствовать римские числа
     * Возвращаем результат вычисления в виде строки (6 или V||)
     */
    public static String calc(String input) throws Exception {
        String[] data = getData(input);
        String res = "";

        // Проверяем систему счисления и вызываем соответствующий метод вычисления
        if (data[0] == "0")
            res = matchArabic(data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]));
        else
            res = matchRoman(data[1], data[2], data[3]);

        return res;
    }

    public static void main(String[] args) throws Exception {
        Scanner console = new Scanner(System.in);
        String line = console.nextLine();
        String result = calc(line);
        System.out.println(result);
    }
}
