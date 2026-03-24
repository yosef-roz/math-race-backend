package com.example.math_race.race.questions;

import com.example.math_race.race.RacePlayer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.math_race.race.questions.AdjectiveType.*;
import static com.example.math_race.race.questions.ItemCategory.*;

@Component
public class MathQuestionGenerator {


    static ArrayList<Human> humans =  new ArrayList<>();
    static ArrayList<Item> items =  new ArrayList<>();
    static ArrayList<Verb> verbs = new ArrayList<>();
    static ArrayList<Place> places = new ArrayList<>();
    public static List<Adjective> adjectives = new ArrayList<>();
    public static List<Unit> units = new ArrayList<>();

    static {
        fillHumans();
        fillVerbs();
        fillItems();
        fillPlaces();
        fillAdjectives();
        fillUnits();
    }


    String template = "[HUMAN:g=?:#1] [VERB:id=buy;g=(#1:g);t=past;num=s:#2] [NUM:min=2;max=10:mul_3:#3] [ITEM:type=FOOD:p:#4]";
// זה יכול להדפיס: "Noa קנתה 5 apples" או "Shimon קנה 3 bananas"

    String t = "[ITEM:param=m;param=?:take:name]";



    public static String gene(String template, Map<String, QuestionEntity> memory) {
        Set<String> tags = extractUniqueTags(template);

        if (memory == null) {
            memory = new HashMap<>();
        }
        String result = template;

        for (String tag : tags) {

            if (tag.startsWith("[IF:")) {
                try {
                    int secondColon = tag.indexOf(":", 4);
                    if (secondColon == -1) continue;
                    String conditionFromTag = tag.substring(4, secondColon);

                    String regex = "\\[IF:" + java.util.regex.Pattern.quote(conditionFromTag) + ":<(.*?)>:<(.*?)>]";
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
                    java.util.regex.Matcher matcher = pattern.matcher(result);

                    if (matcher.find()) {
                        String fullMatchInResult = matcher.group(0);
                        String trueOption = matcher.group(1);
                        String falseOption = matcher.group(2);

                        String operator = "";
                        if (conditionFromTag.contains(">=")) operator = ">=";
                        else if (conditionFromTag.contains("<=")) operator = "<=";
                        else if (conditionFromTag.contains(">")) operator = ">";
                        else if (conditionFromTag.contains("<")) operator = "<";
                        else if (conditionFromTag.contains("=")) operator = "=";

                        if (!operator.isEmpty()) {
                            int opIndex = conditionFromTag.indexOf(operator);
                            String leftSide = conditionFromTag.substring(0, opIndex).trim();
                            String expectedStr = conditionFromTag.substring(opIndex + operator.length()).trim();

                            String entityId = leftSide;
                            String propertyKey = "";

                            if (leftSide.contains(":")) {
                                String[] varParts = leftSide.split(":", 2);
                                entityId = varParts[0].trim();
                                propertyKey = varParts[1].trim();
                            }

                            String actualStr = "";
                            if (memory.containsKey(entityId)) {
                                actualStr = memory.get(entityId).getProperty(propertyKey);
                            }

                            boolean conditionMet = false;
                            if (operator.equals("=")) {
                                conditionMet = actualStr.equals(expectedStr);
                            } else {
                                try {
                                    double actualNum = Double.parseDouble(actualStr);
                                    double expectedNum = Double.parseDouble(expectedStr);
                                    conditionMet = switch (operator) {
                                        case ">" -> actualNum > expectedNum;
                                        case "<" -> actualNum < expectedNum;
                                        case ">=" -> actualNum >= expectedNum;
                                        case "<=" -> actualNum <= expectedNum;
                                        default -> conditionMet;
                                    };
                                } catch (NumberFormatException nfe) {
                                    System.out.println("Warning: Numeric comparison failed for: " + actualStr);
                                }
                            }

                            String chosenText = conditionMet ? trueOption : falseOption;
                            String resolvedText = gene(chosenText, memory);
                            result = result.replace(fullMatchInResult, resolvedText);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing IF tag: " + tag);
                }
                continue;
            }
            TagInfo info = TagInfo.parse(tag);
            if (!tag.startsWith("[#")) {
                Map<String, String> resolvedConstraints = new HashMap<>();

                for (Map.Entry<String, String> entry : info.getConstraints().entrySet()) {
                    resolvedConstraints.put(entry.getKey(), resolveValue(entry.getValue(), memory));
                }

                QuestionEntity chosen = null;
                if ("HUMAN".equals(info.getType())) chosen = findHuman(resolvedConstraints);
                else if ("ITEM".equals(info.getType())) chosen = findItem(resolvedConstraints);
                else if ("NUM".equals(info.getType())) chosen = findNumber(resolvedConstraints);
                else if ("VERB".equals(info.getType())) chosen = findVerb(resolvedConstraints);
                else if ("PLACE".equals(info.getType())) chosen = findPlace(resolvedConstraints);
                else if ("ADJ".equals(info.getType())) chosen = findAdjective(resolvedConstraints);
                else if ("UNIT".equals(info.getType())) chosen = findUnit(resolvedConstraints);
                else if ("TIME".equals(info.getType())) chosen = findTime(resolvedConstraints);

                if (chosen != null) {
                    memory.put(info.getId(), chosen);

                    String property = info.getProperty();
                    String newProperty = splitBeforeAndWithinParentheses(property);
                    String[] splitNewProperty;

                    if (newProperty != null) {
                        splitNewProperty = newProperty.split(":",2);
                        property = splitNewProperty[0] + resolveValue(splitNewProperty[1], memory);
                    }

                    String resolvedProp = resolveValue(property, memory);
                    result = result.replace(tag, !info.getProperty().equals("*") ? chosen.getProperty(resolvedProp) : "");
                }
            } else {
                if (memory.containsKey(info.getId())) {
                    QuestionEntity entity = memory.get(info.getId());

                    String resolvedProp = resolveValue(info.getProperty(), memory);
                    result = result.replace(tag, entity.getProperty(resolvedProp));
                }
            }
        }
        return result;
    }

    public static Set<String> extractUniqueTags(String template) {
        Set<String> tags = new LinkedHashSet<>();

        int indexStart = -1;
        int depth = 0; // המונה שמציל אותנו

        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);

            if (c == '[') {
                if (depth == 0) {
                    indexStart = i;
                }
                depth++;

            } else if (c == ']') {
                if (depth > 0) {
                    depth--;

                    if (depth == 0) {
                        String tag = template.substring(indexStart, i + 1);
                        tags.add(tag);
                        indexStart = -1;
                    }
                }
            }
        }

        return tags;
    }

    private static String resolveValue(String value, Map<String, QuestionEntity> memory) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        boolean isNot = false;
        String expression = value.trim();

        if (expression.startsWith("!(") && expression.endsWith(")")) {
            isNot = true;
            expression = expression.substring(2, expression.length() - 1);
        } else if (expression.startsWith("(") && expression.endsWith(")")) {
            expression = expression.substring(1, expression.length() - 1);
        } else {
            return value;
        }

        String[] parts = expression.split(":", 2);
        String id = parts[0];
        String property = (parts.length > 1) ? parts[1] : "";


        if (memory.containsKey(id)) {
            String newProperty = splitBeforeAndWithinParentheses(property);
            String[] splitNewProperty;

            if (newProperty != null) {
                splitNewProperty = newProperty.split(":",2);
                property = splitNewProperty[0] + resolveValue(splitNewProperty[1], memory);
            }

            String resolvedValue = memory.get(id).getProperty(property);
            return isNot ? "!" + resolvedValue : resolvedValue;
        }

        return value;
    }


    public static String splitBeforeAndWithinParentheses(String str) {
        if (str == null || !str.endsWith(")")) {
            return null;
        }

        int firstOpen = str.indexOf('(');

        if (firstOpen == -1) {
            return null;
        }

        String x = str.substring(0, firstOpen);
        String y = str.substring(firstOpen);

        return x + ":" + y;
    }


    public static Human findHuman(Map<String, String> constraints) {
        List<Human> matches = humans.stream()
                .filter(h -> h.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No human matches constraints: " + constraints);
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static QuestionEntity findAdjective(Map<String, String> constraints) {
        List<Adjective> matches = adjectives.stream()
                .filter(a -> a.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No adjective matches constraints: " + constraints);
            return null;
        }

        Adjective chosenAdjective = matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));

        String g = constraints.getOrDefault("g", "MALE").toUpperCase();
        String num = constraints.getOrDefault("num", "s").toLowerCase();

        String exactWord = chosenAdjective.getWord(g, num);

        return key -> exactWord;
    }

    public static Item findItem(Map<String, String> constraints) {
        List<Item> matches = items.stream()
                .filter(h -> h.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No item matches constraints: " + constraints);
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static Place findPlace(Map<String, String> constraints) {
        List<Place> matches = places.stream()
                .filter(p -> p.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No place matches constraints: " + constraints); // שונה ל-place
            return null;
        }

        return matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static NumberEntity findNumber(Map<String, String> constraints) {
        int min, max;
        try {
            min = Integer.parseInt(constraints.getOrDefault("min", "1").trim());
            max = Integer.parseInt(constraints.getOrDefault("max", "100").trim());

            if (min > max) {
                int temp = min;
                min = max;
                max = temp;
            }
        } catch (NumberFormatException e) {
            min = 1;
            max = 100;
        }

        if (constraints.containsKey("value") && !constraints.get("value").equals("?")) {
            String valStr = constraints.get("value").trim();

            if (valStr.startsWith("!")) {
                try {
                    int forbiddenValue = Integer.parseInt(valStr.substring(1));

                    if (min == max && min == forbiddenValue) {
                        return new NumberEntity(min);
                    }

                    int randomNumber;
                    do {
                        randomNumber = java.util.concurrent.ThreadLocalRandom.current().nextInt(min, max + 1);
                    } while (randomNumber == forbiddenValue);

                    return new NumberEntity(randomNumber);
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Invalid value: " + valStr);
                }
            }

            else {
                try {
                    return new NumberEntity(Integer.parseInt(valStr));
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Invalid value for number: " + valStr);
                }
            }
        }

        int randomNumber = java.util.concurrent.ThreadLocalRandom.current().nextInt(min, max + 1);
        return new NumberEntity(randomNumber);
    }

    public static TimeEntity findTime(Map<String, String> constraints) {
        int minMinutes = 0;       // 00:00 (ברירת מחדל למינימום)
        int maxMinutes = 1439;    // 23:59 (ברירת מחדל למקסימום)

        // 1. קריאת גבולות המינימום והמקסימום
        try {
            if (constraints.containsKey("min") && !constraints.get("min").equals("?")) {
                minMinutes = parseTime(constraints.get("min"));
            }
            if (constraints.containsKey("max") && !constraints.get("max").equals("?")) {
                maxMinutes = parseTime(constraints.get("max"));
            }
        } catch (Exception e) {
            System.out.println("Warning: Invalid time format in min/max constraints");
        }

        // סידור הגבולות למקרה שהזינו הפוך
        if (minMinutes > maxMinutes) {
            int temp = minMinutes;
            minMinutes = maxMinutes;
            maxMinutes = temp;
        }

        boolean round = !constraints.getOrDefault("round", "true").equalsIgnoreCase("false");

        if (constraints.containsKey("value") && !constraints.get("value").equals("?")) {
            String valStr = constraints.get("value").trim();

            if (valStr.startsWith("!")) {
                try {
                    int forbiddenValue = parseTime(valStr.substring(1));

                    if (minMinutes == maxMinutes && minMinutes == forbiddenValue) {
                        return new TimeEntity(minMinutes);
                    }

                    int randomMinutes;
                    int attempts = 0;

                    do {
                        randomMinutes = java.util.concurrent.ThreadLocalRandom.current().nextInt(minMinutes, maxMinutes + 1);

                        if (round) {
                            randomMinutes = Math.round(randomMinutes / 5.0f) * 5;
                            if (randomMinutes > maxMinutes) randomMinutes = maxMinutes;
                            if (randomMinutes < minMinutes) randomMinutes = minMinutes;
                        }

                        attempts++;
                        if (attempts > 100) break;

                    } while (randomMinutes == forbiddenValue);

                    return new TimeEntity(randomMinutes);

                } catch (Exception e) {
                    System.out.println("Warning: Invalid time format for forbidden value: " + valStr);
                }
            }
            else {
                try {
                    return new TimeEntity(parseTime(valStr));
                } catch (Exception e) {
                    System.out.println("Warning: Invalid time format for specific value: " + valStr);
                }
            }
        }

        int randomMinutes = java.util.concurrent.ThreadLocalRandom.current().nextInt(minMinutes, maxMinutes + 1);

        if (round) {
            randomMinutes = Math.round(randomMinutes / 5.0f) * 5;
            if (randomMinutes > maxMinutes) randomMinutes = maxMinutes;
            if (randomMinutes < minMinutes) randomMinutes = minMinutes;
        }

        return new TimeEntity(randomMinutes);
    }

    private static int parseTime(String timeStr) {
        String[] parts = timeStr.trim().split("[:.]");
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        return h * 60 + m;
    }

    public static QuestionEntity findVerb(Map<String, String> constraints) {
        List<Verb> matches = verbs.stream()
                .filter(v -> v.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No verb matches constraints: " + constraints);
            return null;
        }

        Verb chosenVerb = matches.get(ThreadLocalRandom.current().nextInt(matches.size()));

        String f = constraints.getOrDefault("f", "regular").toLowerCase();
        String t = constraints.getOrDefault("t", "past").toLowerCase();
        String g = constraints.getOrDefault("g", "MALE").toUpperCase();
        String num = constraints.getOrDefault("num", "s").toLowerCase();

        if (f.equals("inf")) {
            return key -> chosenVerb.getWord("inf", "ANY", "ANY");
        }

        String exactWord = chosenVerb.getWord(t, g, num);

        return key -> exactWord;
    }

    public static QuestionEntity findUnit(Map<String, String> constraints) {
        List<Unit> matches = units.stream()
                .filter(u -> u.matches(constraints))
                .toList();

        if (matches.isEmpty()) {
            System.out.println("Warning: No unit matches constraints: " + constraints);
            return null;
        }

        return matches.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(matches.size()));
    }

    public static void fillHumans() {
        humans.add(new Human("שמעון", Gender.MALE));
        humans.add(new Human("יוסף", Gender.MALE));
        humans.add(new Human("אברהם", Gender.MALE));
        humans.add(new Human("דוד", Gender.MALE));

        humans.add(new Human("נועה", Gender.FEMALE));
        humans.add(new Human("תמר", Gender.FEMALE));
        humans.add(new Human("יעל", Gender.FEMALE));
        humans.add(new Human("מאיה", Gender.FEMALE));
    }

    public static void fillItems() {
        // הגדרת קבוצות יחידות מראש לשימוש חוזר
        Set<UnitType> weightOnly = Set.of(UnitType.WEIGHT);
        Set<UnitType> volumeOnly = Set.of(UnitType.VOLUME);
        Set<UnitType> countOnly = Set.of(UnitType.COUNT);
        Set<UnitType> lengthOnly = Set.of(UnitType.LENGTH);
        Set<UnitType> weightOrCount = Set.of(UnitType.WEIGHT, UnitType.COUNT);

        // --- FOOD (אוכל) ---
        // פירות בדרך כלל נשקלים או נספרים
        items.add(new Item("תפוח", "תפוחים", Gender.MALE, weightOrCount, ItemCategory.FOOD, ItemCategory.GENERAL));
        items.add(new Item("בננה", "בננות", Gender.FEMALE, weightOrCount, ItemCategory.FOOD, ItemCategory.GENERAL));
        items.add(new Item("תפוז", "תפוזים", Gender.MALE, weightOrCount, ItemCategory.FOOD, ItemCategory.GENERAL));
        // ממתקים נשקלים או נספרים
        items.add(new Item("סוכרייה", "סוכריות", Gender.FEMALE, weightOrCount, ItemCategory.FOOD, ItemCategory.GENERAL));
        // פיצה נמדדת ביחידות (מגשים/משולשים)
        items.add(new Item("פיצה", "פיצות", Gender.FEMALE, countOnly, ItemCategory.FOOD, ItemCategory.GENERAL));
        // הוספת שתייה (נפח)
        items.add(new Item("מיץ", "מיצים", Gender.MALE, volumeOnly, ItemCategory.FOOD));
        items.add(new Item("מים", "בקבוקי מים", Gender.MALE, volumeOnly, ItemCategory.FOOD));

        // --- COLLECTIBLE (אספנות) ---
        // גולות, קלפים ומדבקות רק נספרים
        items.add(new Item("גולה", "גולות", Gender.FEMALE, countOnly, ItemCategory.COLLECTIBLE, ItemCategory.GENERAL));
        items.add(new Item("קלף", "קלפים", Gender.MALE, countOnly, ItemCategory.COLLECTIBLE, ItemCategory.GENERAL));
        items.add(new Item("מדבקה", "מדבקות", Gender.FEMALE, countOnly, ItemCategory.COLLECTIBLE, ItemCategory.GENERAL));
        items.add(new Item("גלויה", "גלויות", Gender.FEMALE, countOnly, ItemCategory.COLLECTIBLE, ItemCategory.GENERAL));
        // מטבע זהב אפשר לספור אבל גם לשקול (בגלל הערך של הזהב)
        items.add(new Item("מטבע זהב", "מטבעות זהב", Gender.MALE, weightOrCount, ItemCategory.MONEY, ItemCategory.COLLECTIBLE, ItemCategory.GENERAL));

        // --- STATIONERY (ציוד לימודי) ---
        // רוב הציוד נספר ביחידות
        items.add(new Item("עיפרון", "עפרונות", Gender.MALE, countOnly, ItemCategory.STATIONERY, ItemCategory.GENERAL));
        items.add(new Item("מחברת", "מחברות", Gender.FEMALE, countOnly, ItemCategory.STATIONERY, ItemCategory.GENERAL));
        items.add(new Item("ספר", "ספרים", Gender.MALE, countOnly, ItemCategory.STATIONERY, ItemCategory.GENERAL));
        items.add(new Item("מחק", "מחקים", Gender.MALE, countOnly, ItemCategory.STATIONERY, ItemCategory.GENERAL));
        // סרט הדבקה או חוט נמדדים באורך
        items.add(new Item("סרט הדבקה", "סרטי הדבקה", Gender.MALE, lengthOnly, ItemCategory.STATIONERY));

        // --- TOY (צעצועים) ---
        items.add(new Item("כדור", "כדורים", Gender.MALE, countOnly, ItemCategory.TOY, ItemCategory.GENERAL));
        items.add(new Item("בובה", "בובות", Gender.FEMALE, countOnly, ItemCategory.TOY, ItemCategory.GENERAL));
        items.add(new Item("מכונית צעצוע", "מכוניות צעצוע", Gender.FEMALE, countOnly, ItemCategory.TOY, ItemCategory.GENERAL));

        // --- MONEY (כסף) ---
        // כסף נספר ביחידות (שטרות/מטבעות)
        items.add(new Item("שקל", "שקלים", Gender.MALE, countOnly, ItemCategory.MONEY, ItemCategory.GENERAL));
        items.add(new Item("שטר", "שטרות", Gender.MALE, countOnly, ItemCategory.MONEY, ItemCategory.GENERAL));
    }

    public static void fillVerbs() {
        // --- קנה (מתאים ל: MONEY, GENERAL) ---
        Verb buy = new Verb("buy");
        buy.addForm("past", "MALE", "s", "קנה");
        buy.addForm("past", "FEMALE", "s", "קנתה");
        buy.addForm("past", "MALE", "p", "קנו");
        buy.addForm("past", "FEMALE", "p", "קנו");
        buy.addForm("inf", "ANY", "ANY", "לקנות");
        verbs.add(buy);

        // --- אכל (מתאים ל: FOOD) ---
        Verb eat = new Verb("eat");
        eat.addForm("past", "MALE", "s", "אכל");
        eat.addForm("past", "FEMALE", "s", "אכלה");
        eat.addForm("past", "MALE", "p", "אכלו");
        eat.addForm("past", "FEMALE", "p", "אכלו");
        eat.addForm("inf", "ANY", "ANY", "לאכול");
        verbs.add(eat);

        // --- נתן (מתאים ל: COLLECTIBLE, GENERAL) ---
        Verb give = new Verb("give");
        give.addForm("past", "MALE", "s", "נתן");
        give.addForm("past", "FEMALE", "s", "נתנה");
        give.addForm("past", "MALE", "p", "נתנו");
        give.addForm("past", "FEMALE", "p", "נתנו");
        give.addForm("inf", "ANY", "ANY", "לתת");
        verbs.add(give);

        // --- קיבל (מתאים ל: COLLECTIBLE, GENERAL, MONEY) ---
        Verb receive = new Verb("receive");
        receive.addForm("past", "MALE", "s", "קיבל");
        receive.addForm("past", "FEMALE", "s", "קיבלה");
        receive.addForm("past", "MALE", "p", "קיבלו");
        receive.addForm("past", "FEMALE", "p", "קיבלו");
        receive.addForm("inf", "ANY", "ANY", "לקבל");
        verbs.add(receive);

        // --- מצא (מתאים ל: COLLECTIBLE, GENERAL) ---
        Verb find = new Verb("find");
        find.addForm("past", "MALE", "s", "מצא");
        find.addForm("past", "FEMALE", "s", "מצאה");
        find.addForm("past", "MALE", "p", "מצאו");
        find.addForm("past", "FEMALE", "p", "מצאו");
        find.addForm("inf", "ANY", "ANY", "למצוא");
        verbs.add(find);

        // --- איבד (מתאים ל: COLLECTIBLE, GENERAL) ---
        Verb lose = new Verb("lose");
        lose.addForm("past", "MALE", "s", "איבד");
        lose.addForm("past", "FEMALE", "s", "איבדה");
        lose.addForm("past", "MALE", "p", "איבדו");
        lose.addForm("past", "FEMALE", "p", "איבדו");
        lose.addForm("inf", "ANY", "ANY", "לאבד");
        verbs.add(lose);

        // --- אסף (מתאים ל: COLLECTIBLE) ---
        Verb collect = new Verb("collect");
        collect.addForm("past", "MALE", "s", "אסף");
        collect.addForm("past", "FEMALE", "s", "אספה");
        collect.addForm("past", "MALE", "p", "אספו");
        collect.addForm("past", "FEMALE", "p", "אספו");
        collect.addForm("inf", "ANY", "ANY", "לאסוף");
        verbs.add(collect);

        // --- חילק (מתאים ל: שאלות חילוק - FOOD, COLLECTIBLE) ---
        Verb divide = new Verb("divide");
        divide.addForm("past", "MALE", "s", "חילק");
        divide.addForm("past", "FEMALE", "s", "חילקה");
        divide.addForm("past", "MALE", "p", "חילקו");
        divide.addForm("past", "FEMALE", "p", "חילקו");
        divide.addForm("inf", "ANY", "ANY", "לחלק");
        verbs.add(divide);

        // --- נכנס (מתאים ל: PLACE) ---
        Verb enter = new Verb("enter");
        enter.addForm("past", "MALE", "s", "נכנס");
        enter.addForm("past", "FEMALE", "s", "נכנסה");
        enter.addForm("past", "MALE", "p", "נכנסו");
        enter.addForm("past", "FEMALE", "p", "נכנסו");
        enter.addForm("inf", "ANY", "ANY", "להיכנס");
        verbs.add(enter);

        // --- היה (פועל עזר) ---
        Verb be = new Verb("be");
        be.addForm("past", "MALE", "s", "היה");
        be.addForm("past", "FEMALE", "s", "הייתה");
        be.addForm("past", "MALE", "p", "היו");
        be.addForm("past", "FEMALE", "p", "היו");
        be.addForm("inf", "ANY", "ANY", "להיות");
        verbs.add(be);

        // ==========================================
        //            פעלים חדשים שנוספו
        // ==========================================

        // --- מכר (מתאים ל: חנויות, כסף - שאלות של חיסור מהמלאי או הוספת כסף) ---
        Verb sell = new Verb("sell");
        sell.addForm("past", "MALE", "s", "מכר");
        sell.addForm("past", "FEMALE", "s", "מכרה");
        sell.addForm("past", "MALE", "p", "מכרו");
        sell.addForm("past", "FEMALE", "p", "מכרו");
        sell.addForm("inf", "ANY", "ANY", "למכור");
        verbs.add(sell);

        // --- לקח (מתאים לחיסור: מישהו לקח משהו) ---
        Verb take = new Verb("take");
        take.addForm("past", "MALE", "s", "לקח");
        take.addForm("past", "FEMALE", "s", "לקחה");
        take.addForm("past", "MALE", "p", "לקחו");
        take.addForm("past", "FEMALE", "p", "לקחו");
        take.addForm("inf", "ANY", "ANY", "לקחת");
        verbs.add(take);

        // --- שם / הניח (מתאים לחיבור: לשים דברים בקופסה, על מדף) ---
        Verb put = new Verb("put");
        put.addForm("past", "MALE", "s", "שם");
        put.addForm("past", "FEMALE", "s", "שמה");
        put.addForm("past", "MALE", "p", "שמו");
        put.addForm("past", "FEMALE", "p", "שמו");
        put.addForm("inf", "ANY", "ANY", "לשים");
        verbs.add(put);

        // --- סידר (מתאים לכפל: סידר דברים בשורות או במדפים) ---
        Verb arrange = new Verb("arrange");
        arrange.addForm("past", "MALE", "s", "סידר");
        arrange.addForm("past", "FEMALE", "s", "סידרה");
        arrange.addForm("past", "MALE", "p", "סידרו");
        arrange.addForm("past", "FEMALE", "p", "סידרו");
        arrange.addForm("inf", "ANY", "ANY", "לסדר");
        verbs.add(arrange);

        // --- שילם (מתאים לשאלות של קניות ועודף) ---
        Verb pay = new Verb("pay");
        pay.addForm("past", "MALE", "s", "שילם");
        pay.addForm("past", "FEMALE", "s", "שילמה");
        pay.addForm("past", "MALE", "p", "שילמו");
        pay.addForm("past", "FEMALE", "p", "שילמו");
        pay.addForm("inf", "ANY", "ANY", "לשלם");
        verbs.add(pay);

        // --- חסך (מתאים לשאלות של איסוף כסף לאורך זמן) ---
        Verb save = new Verb("save");
        save.addForm("past", "MALE", "s", "חסך");
        save.addForm("past", "FEMALE", "s", "חסכה");
        save.addForm("past", "MALE", "p", "חסכו");
        save.addForm("past", "FEMALE", "p", "חסכו");
        save.addForm("inf", "ANY", "ANY", "לחסוך");
        verbs.add(save);

        // --- סיים ---
        Verb finish = new Verb("finish");
        finish.addForm("past", "MALE", "s", "סיים");
        finish.addForm("past", "FEMALE", "s", "סיימה");
        finish.addForm("past", "MALE", "p", "סיימו");
        finish.addForm("past", "FEMALE", "p", "סיימו");
        finish.addForm("inf", "ANY", "ANY", "לסיים");
        verbs.add(finish);
    }

    public static void fillPlaces(){
        // --- חנויות ועסקים (STORE) ---
        places.add(new Place("מכולת", "מכולות", Gender.FEMALE, PlaceType.STORE, FOOD, GENERAL));
        places.add(new Place("סופרמרקט", "סופרמרקטים", Gender.MALE, PlaceType.STORE, FOOD, GENERAL));
        places.add(new Place("מאפייה", "מאפיות", Gender.FEMALE, PlaceType.STORE, FOOD));
        places.add(new Place("קיוסק", "קיוסקים", Gender.MALE, PlaceType.STORE, FOOD));
        places.add(new Place("שוק", "שווקים", Gender.MALE, PlaceType.STORE, FOOD, COLLECTIBLE, GENERAL));
        places.add(new Place("חנות צעצועים", "חנויות צעצועים", Gender.FEMALE, PlaceType.STORE, TOY));
        places.add(new Place("חנות עתיקות", "חנויות עתיקות", Gender.FEMALE, PlaceType.STORE, COLLECTIBLE));
        places.add(new Place("חנות יצירה", "חנויות יצירה", Gender.FEMALE, PlaceType.STORE, STATIONERY, TOY));
        places.add(new Place("חנות כלי כתיבה", "חנויות כלי כתיבה", Gender.FEMALE, PlaceType.STORE, STATIONERY, GENERAL));
        places.add(new Place("קניון", "קניונים", Gender.MALE, PlaceType.STORE, GENERAL, FOOD, TOY, STATIONERY, MONEY));
        places.add(new Place("חנות", "חנויות", Gender.FEMALE, PlaceType.STORE, GENERAL, FOOD, TOY));

        // --- מקומות ציבוריים (PUBLIC) ---
        places.add(new Place("ספרייה", "ספריות", Gender.FEMALE, PlaceType.PUBLIC, STATIONERY, GENERAL));
        places.add(new Place("בנק", "בנקים", Gender.MALE, PlaceType.PUBLIC, MONEY));
        places.add(new Place("קופה", "קופות", Gender.FEMALE, PlaceType.PUBLIC, MONEY)); // יכול להיות גם בתוך חנות
        places.add(new Place("כספומט", "כספומטים", Gender.MALE, PlaceType.PUBLIC, MONEY));
        places.add(new Place("משחקייה", "משחקיות", Gender.FEMALE, PlaceType.PUBLIC, TOY));
        places.add(new Place("בית ספר", "בתי ספר", Gender.MALE, PlaceType.PUBLIC, STATIONERY, GENERAL)); // חדש

        // --- מקומות בחוץ (OUTDOORS) ---
        places.add(new Place("פארק", "פארקים", Gender.MALE, PlaceType.OUTDOORS, TOY, COLLECTIBLE, GENERAL));
        places.add(new Place("גינה", "גינות", Gender.FEMALE, PlaceType.OUTDOORS, TOY, GENERAL)); // חדש
        places.add(new Place("רחוב", "רחובות", Gender.MALE, PlaceType.OUTDOORS, GENERAL)); // חדש
        places.add(new Place("חצר", "חצרות", Gender.FEMALE, PlaceType.OUTDOORS, GENERAL, TOY)); // חדש

        // --- מקומות פרטיים (HOME) ---
        places.add(new Place("בית", "בתים", Gender.MALE, PlaceType.HOME, GENERAL, FOOD, TOY, COLLECTIBLE));
        places.add(new Place("חדר", "חדרים", Gender.MALE, PlaceType.HOME, GENERAL, TOY, STATIONERY)); // חדש
    }

    public static void fillAdjectives() {
        // --- צבעים ---
        Adjective red = new Adjective("red", COLOR);
        red.addForm("MALE", "s", "אדום");
        red.addForm("FEMALE", "s", "אדומה");
        red.addForm("MALE", "p", "אדומים");
        red.addForm("FEMALE", "p", "אדומות");
        adjectives.add(red);

        Adjective blue = new Adjective("blue", COLOR);
        blue.addForm("MALE", "s", "כחול");
        blue.addForm("FEMALE", "s", "כחולה");
        blue.addForm("MALE", "p", "כחולים");
        blue.addForm("FEMALE", "p", "כחולות");
        adjectives.add(blue);

        // --- גדלים ---
        Adjective big = new Adjective("big", SIZE);
        big.addForm("MALE", "s", "גדול");
        big.addForm("FEMALE", "s", "גדולה");
        big.addForm("MALE", "p", "גדולים");
        big.addForm("FEMALE", "p", "גדולות");
        adjectives.add(big);

        Adjective small = new Adjective("small", SIZE);
        small.addForm("MALE", "s", "קטן");
        small.addForm("FEMALE", "s", "קטנה");
        small.addForm("MALE", "p", "קטנים");
        small.addForm("FEMALE", "p", "קטנות");
        adjectives.add(small);

        // --- תכונות שונות ---
        Adjective newAdj = new Adjective("new", CONDITION);
        newAdj.addForm("MALE", "s", "חדש");
        newAdj.addForm("FEMALE", "s", "חדשה");
        newAdj.addForm("MALE", "p", "חדשים");
        newAdj.addForm("FEMALE", "p", "חדשות");
        adjectives.add(newAdj);

        Adjective oldAdj = new Adjective("old", CONDITION);
        oldAdj.addForm("MALE", "s", "ישן");
        oldAdj.addForm("FEMALE", "s", "ישנה");
        oldAdj.addForm("MALE", "p", "ישנים");
        oldAdj.addForm("FEMALE", "p", "ישנות");
        adjectives.add(oldAdj);
    }

    public static void fillUnits() {
        // --- משקל ---
        units.add(new Unit("kg", "קילוגרם", "קילוגרמים", Gender.MALE, UnitType.WEIGHT));
        units.add(new Unit("gram", "גרם", "גרמים", Gender.MALE, UnitType.WEIGHT));
        units.add(new Unit("ton", "טון", "טונות", Gender.MALE, UnitType.WEIGHT));

        units.add(new Unit("meter", "מטר", "מטרים", Gender.MALE, UnitType.LENGTH));
        units.add(new Unit("cm", "סנטימטר", "סנטימטרים", Gender.MALE, UnitType.LENGTH));
        units.add(new Unit("km", "קילומטר", "קילומטרים", Gender.MALE, UnitType.LENGTH));

        units.add(new Unit("liter", "ליטר", "ליטרים", Gender.MALE, UnitType.VOLUME));
        units.add(new Unit("ml", "מיליליטר", "מיליליטרים", Gender.MALE, UnitType.VOLUME));
        units.add(new Unit("cup", "כוס", "כוסות", Gender.FEMALE, UnitType.VOLUME));

        // --- יחידות ספירה (COUNT) ---
        units.add(new Unit("piece", "יחידה", "יחידות", Gender.FEMALE, UnitType.COUNT));
        units.add(new Unit("box", "מארז", "מארזים", Gender.MALE, UnitType.COUNT));
        units.add(new Unit("tray", "מגש", "מגשים", Gender.MALE, UnitType.COUNT));
    }







    public MathQuestion generateForPlayer(RacePlayer player) {
        String expression = "המלך ביקש מעידן שיקנה לו 3 תפוחים, עידן קנה 3 תפוחים והביא מהבית עוד 2 ונתן הכל למלך. כמה תפוחים סהכ הביא עידן למלך ?";
        List<String> options = List.of("6","3","5","2");
        String correctAnswer = "5";
        int timeLimitSeconds = 15;
        int score = 20;

        return new MathQuestion(expression,options,correctAnswer,timeLimitSeconds,score);
    }
}
