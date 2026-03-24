package com.example.math_race;

import com.example.math_race.race.questions.MathQuestionGenerator;
import com.example.math_race.race.questions.QuestionEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {


    public static void main(String[] args) {

        System.out.println("\n----------------------------------------------------------");
        System.out.println("  Application 'Math Race' is running successfully!");
        System.out.println("----------------------------------------------------------\n");

        Map<String, QuestionEntity> memory = new HashMap<>();


        String template1 = "[HUMAN::#1] [VERB:id=find;t=past;g=(#1:g);num=s:#V1] [NUM:min=10;max=20:#X] [ITEM:type=COLLECTIBLE:p:#2]. " +
                "[#1:he_she] [VERB:id=give;t=past;g=(#1:g);num=s:#V2] ל[HUMAN:n=!(#1:n):#3] [NUM:min=2;max=5:#Y] [#2:p]. " +
                "כמה [#2:p] נשארו ל[#1]?";

        String template1Result = "[NUM:value=(#X:add_-(#Y)):#R]";

        String c= "[NUM:min=0;max=1:hide:#W] [IF:#W=1:<How many intact [#1:p] remained?>:<How many [#1:p] were destroyed?>]";

        System.out.println("==============================");

        //System.out.println(extractUniqueTags(c));

        List<String[]> templates = new ArrayList<>();

        // 1. חיסור (כמה נשארו)
        templates.add(new String[]{
                "[HUMAN:g=m:#1] [VERB:id=find;t=past;g=(#1:g);num=s:#V1] [NUM:min=10;max=20:#X] [ITEM:type=COLLECTIBLE:p:#2]. " +
                        "[#1:he_she] [VERB:id=give;t=past;g=(#1:g);num=s:#V2] ל[HUMAN:n=!(#1:n):#3] [NUM:min=2;max=5:#Y] [#2:p]. " +
                        "כמה [#2:p] נשארו ל[#1]?",
                "[NUM:value=(#X:add_-(#Y)):#R]"
        });

        // 2. כפל (ארגזים ומארזים)
        templates.add(new String[]{
                "ב[PLACE:type=FOOD:s:#P1] יש [NUM:min=3;max=6:#X] ארגזים. " +
                        "בכל ארגז יש [NUM:min=5;max=10:#Y] [ITEM:type=FOOD:p:#1]. " +
                        "כמה [#1:p] יש בסך הכל [#P1:in_it]?",
                "[NUM:value=(#X:mul_(#Y)):#R]"
        });

        // 3. השוואה (למי יש יותר) - דורש שתי פעולות חיבור
        templates.add(new String[]{
                "ל[HUMAN:n:#1] יש [NUM:min=10;max=30:#X] [ITEM:type=COLLECTIBLE:p:#2]. " +
                        "ל[HUMAN:n=!(#1:n):#3] יש [NUM:min=5;max=15:#Y] [#2:p] יותר מאשר ל[#1]. " +
                        "כמה [#2:p] יש לשניהם ביחד?",
                "[NUM:value=(#X:add_(#X:add_(#Y))):#R]"
        });

        // 4. חילוק (חלוקה שווה)
        templates.add(new String[]{
                "[HUMAN:#1] [VERB:id=buy;g=(#1:g);t=past] [NUM:min=12;max=24:#X] [ITEM:type=STATIONERY:p:#2]. " +
                        "[#1:he_she] רוצה לחלק אותם שווה בשווה בין [NUM:min=2;max=4:#Y] חברות [#1:his_hers]. " +
                        "כמה [#2:p] כל חברה תקבל?",
                "[NUM:value=(#X:div_(#Y)):#R]"
        });

        // 5. קניות ועודף (שילוב כפל וחיסור)
        templates.add(new String[]{
                "[HUMAN:#1] נכנס ל[PLACE:type=TOY;place_type=STORE:s:#P1]. " +
                        "[#1:he_she] [VERB:id=buy;g=(#1:g);t=past] [NUM:min=2;max=4:#X] [ITEM:type=TOY:p:#2], " +
                        "כאשר כל [#2:s] עולה [NUM:min=5;max=10:#Y] שקלים. " +
                        "אם [#1:he_she] שילם בשטר של [NUM:value=50:#Z] שקלים, כמה עודף נשאר?",
                "[NUM:value=(#Z:sub_(#X:mul_(#Y))):#R]"
        });

        templates.add(new String[]{
                "[HUMAN:#1] [VERB:id=find;g=(#1:g);t=past] [NUM:min=5;max=15:#X] [ITEM:type=COLLECTIBLE:p:#2] ב[PLACE:place_type=OUTDOORS:s:#P1]. " +
                        "גם [HUMAN:n=!(#1:n):#3] [VERB:id=find;g=(#3:g);t=past] [NUM:min=5;max=15:#Y] [#2:p] [#P1:in_it]. " +
                        "הם החליטו לחלק את כל ה[#2:p] שווה בשווה בין [NUM:min=2;max=4:#Z] חברים. " +
                        "כמה [#2:p] קיבל כל חבר?",
                "[NUM:value=(#X:add_(#Y)):div_(#Z):#R]"
        });

        templates.add(new String[]{
                "ל[HUMAN:#1] יש [NUM:min=3;max=5:#X] קופסאות של [ITEM:type=FOOD|TOY:p:#2]. " +
                        "בכל קופסה יש [NUM:min=6;max=10:#Y] [#2:p]. " +
                        "[#1:he_she] [VERB:id=give;g=(#1:g);t=past] ל[HUMAN:n=!(#1:n):#3] [NUM:min=5;max=10:#Z] [#2:p]. " +
                        "כמה [#2:p] נשארו ל[#1]?",
                "[NUM:value=(#X:mul_(#Y)):sub_(#Z):#R]"
        });

        templates.add(new String[]{
                "ל[HUMAN:g=f:#1] יש [NUM:min=20;max=40:#X] [ITEM:type=STATIONERY:p:#2]. " +
                        "ל[HUMAN:g=m:#3] יש [NUM:min=5;max=15:#Y] [#2:p] **פחות** מאשר ל[#1]. " +
                        "כמה [#2:p] יש ל[#1] ול[#3] ביחד?",
                "[NUM:value=(#X:add_(#X:sub_(#Y))):#R]"
        });

        templates.add(new String[]{
                "[HUMAN:#1] [VERB:id=enter;g=(#1:g);t=past] ל[PLACE:place_type=STORE:s:#P1]. " +
                        "[#1:he_she] [VERB:id=buy;g=(#1:g);t=past] [NUM:min=2;max=3:#X] [ITEM:type=(#P1:t):p:#2] שעולים [NUM:min=4;max=6:#Y] שקלים כל [#2:one], " +
                        "וגם [ITEM:type=(#P1:t):s:#3] [#3:one] שעולה [NUM:min=10;max=15:#W] שקלים. " +
                        "אם [#1:he_she] נתן שטר של [NUM:value=50:#Z] שקלים, כמה עודף [#1:he_she] יקבל?",
                "[NUM:value=(#Z:sub_(#X:mul_(#Y))):sub_(#W):#R]"
        });

        templates.add(new String[]{
                "ב[PLACE:place_type=HOME:s:#P1] היו [NUM:min=20;max=50:#X] [ITEM:type=MONEY:p:#2]. " +
                        "חילקו את ה[#2:p] שווה בשווה בין [NUM:min=2;max=5:#Y] ילדים. " +
                        "[HUMAN:#3], [#3:one] הילדים, [VERB:id=lose;g=(#3:g);t=past] [NUM:min=1;max=3:#Z] מה[#2:p] [#3:his_hers]. " +
                        "כמה [#2:p] נשארו ל[#3]?",
                "[NUM:value=(#X:div_(#Y)):sub_(#Z):#R]"
        });
        //
        templates.add(new String[]{
                "[HUMAN:#1] [#1:loves] מאוד [ITEM:type=FOOD:p:#2]. [#1:he_she] [VERB:id=eat;g=(#1:g);t=past] [NUM:min=2;max=5:#X] [#2:p] בכל יום במשך [NUM:min=3;max=7:#Y] ימים ברצף. " +
                        "לאחר מכן, [#1:he_she] [VERB:id=eat;g=(#1:g);t=past] עוד [NUM:min=1;max=4:#Z] [#2:p] ביום האחרון. " +
                        "כמה [#2:p] סך הכל [#1:he_she] [VERB:id=eat;g=(#1:g);t=past]?",
                "[NUM:value=(#X:mul_(#Y)):add_(#Z):#R]"
        });

        templates.add(new String[]{
                "ב[PLACE:place_type=STORE:s:#P1] מסדרים מחדש את המלאי. " +
                        "יש שם [NUM:min=8;max=15:#X] מדפים, ועל כל מדף שמו [NUM:min=6;max=12:#Y] [ITEM:type=(#P1:t):p:#1]. " +
                        "לפתע [NUM:min=2;max=(#X:v):#Z] מדפים נשברו וכל ה[#1:p] שעליהם נהרסו. " +
                        "[NUM:min=0;max=1:*:#5]" +
                        "[IF:#5=0:<" +
                        "כמה [#1:p] שלמים נשארו [#P1:in_it]?" +
                        ">:<" +
                        "כמה [#1:p] נהרסו [#P1:in_it]?" +
                        ">]",

                "[IF:#5=0:<" +
                        "[NUM:value=(#X:sub_(#Z)):mul_(#Y):#R]" +
                        ">:<" +
                        "[NUM:value=(#Z):mul_(#Y):#R]" +
                        ">]"
        });

        templates.add(new String[]{
                // --- חלק 1: בניית הסיפור ---
                "[HUMAN:#1] [VERB:id=enter;t=past;g=(#1:g);num=s] ל[PLACE:place_type=STORE;type=FOOD|STATIONERY:s:#P1] בשעה [TIME:min=08.00;max=11.00:#T1] " +
                        "כדי [VERB:id=buy;f=inf] [ITEM:type=(#P1:t);unit_type=WEIGHT|COUNT:p:#2] [ADJ:id=new;g=(#2:g);num=p:#A1]. " +

                        // כאן קורה הקסם: היחידה (U1) נבחרת לפי מה שהפריט (#2) מרשה!
                        "[#1:he_she] [VERB:id=buy;t=past;g=(#1:g);num=s] [NUM:min=3;max=8:#X] [UNIT:type=(#2:allowed_unit):p:#U1] של [#2:p] [#A1]. " +

                        "כל [#U1:s] עולה [NUM:min=5;max=15:#Y] שקלים. " +
                        "תהליך הקנייה לקחה [#1:to_him_her] בדיוק [NUM:min=15;max=45:#M] דקות. " +

                        // --- חלק 2: פיצול השאלה (משתנה נסתר W) ---
                        "[NUM:min=0;max=1:*:#W]" +
                        "[IF:#W=0:<" +
                        "אם [#1:he_she] שילם בקופה בשטר של [NUM:value=200:#Z] שקלים, כמה עודף [#1:he_she] [VERB:id=receive;t=past;g=(#1:g);num=s]?" +
                        ">:<" +
                        "באיזו שעה [#1:he_she] [VERB:id=finish;t=past;g=(#1:g);num=s] את הקנייה ו[VERB:id=take;t=past;g=(#1:g);num=s] את ה[#2:p]?" +
                        ">]",

                // --- חלק 3: פיצול התשובה (חייב להתאים ל-W) ---
                "[IF:#W=0:<" +
                        "[NUM:value=(#Z:sub_(#X:mul_(#Y))):#R]" + // חישוב עודף
                        ">:<" +
                        "[TIME:value=(#T1:add_m_(#M)):#R]" +     // חישוב זמן סיום
                        ">]"
        });



        // הרצה של המנוע
        System.out.println("--- מריץ ייצור שאלות ---");

        for (int i = 0; i < templates.size(); i++) {
            memory = new HashMap<>(); // זיכרון נקי לכל שאלה
            String[] pair = templates.get(i);

            String question = MathQuestionGenerator.gene(pair[0], memory);
            String answer = MathQuestionGenerator.gene(pair[1], memory);

            System.out.println("שאלה " + (i+1) + ": " + question);
            System.out.println("תשובה נכונה: " + answer);
            System.out.println("-------------------------");
        }
    }
}
