package com.example.math_race;

import com.example.math_race.json.loader.JsonOnlyDictionaryProvider;
import com.example.math_race.json.models.seeders.DictionaryJsonSeeder;
import com.example.math_race.questionGenerator.QuestionEngine;
import com.example.math_race.questionGenerator.dictionary.DictionaryProvider;
import com.example.math_race.questionGenerator.tags.core.TemplateTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {


    public static void main(String[] args) {

        System.out.println("\n----------------------------------------------------------");
        System.out.println("  Application 'Math Race' is running successfully!");
        System.out.println("----------------------------------------------------------\n");

        DictionaryJsonSeeder seeder = new DictionaryJsonSeeder();
        DictionaryProvider jsonProvider = new JsonOnlyDictionaryProvider(seeder);
        QuestionEngine questionEngine = new QuestionEngine(jsonProvider);
        questionEngine.initDictionaryCache();
        Map<String, TemplateTag> memory;



        String template1 = "[HUMAN:n:#1] [VERB:id=find;t=past;g=(#1:g);num=s:#V1] [NUM:min=10;max=20:#X] [ITEM:type=COLLECTIBLE:p:#2]. " +
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


        templates.add(new String[]{
                // 1. הגרלות נסתרות (מגדר מוכר, מקום, והתפקיד עצמו)
                "[NUM:min=0;max=1:*:#OG]" +
                        "[PLACE:place_type=STORE:*:#P1]" +
                        "[ROLE:place_id=(#P1:id);role_type=OPERATOR:*:#O1]" + // יצירת המוכר בזיכרון בלי להדפיס

                        // 2. הסיפור
                        "[HUMAN:#1] [VERB:id=enter;t=past;g=(#1:g);num=s] ל[#P1:s]. " +
                        "ה[IF:#OG=0:<[#O1:m_s] [VERB:id=offer;t=past;g=MALE;num=s]>:<[#O1:f_s] [VERB:id=offer;t=past;g=FEMALE;num=s]>] " +
                        "[#1:to_him_her] [VERB:id=buy;f=inf] [ITEM:type=(#P1:t):p:#2] [ADJ:id=new;g=(#2:g);num=p:#A1]. " +

                        "[#1:n] [VERB:id=want;t=past;g=(#1:g);num=s] [NUM:min=2;max=8:#X] " +
                        // טיפול ברווחים: הרווח נמצא בתוך ה-IF כדי שלא יהיה רווח כפול כשאין יחידה
                        "[UNIT:type=(#2:allowed_unit);item_category=(#2:t):p:#U1][IF:#2:allowed_unit=NONE:<>:< של>] [#2:p] [#A1]. " +

                        "המחיר עבור כל [IF:#2:allowed_unit=NONE:<[#2:s]>:<[#U1:s]>] הוא [NUM:min=5;max=20:#Y] שקלים. " +

                        // 3. השאלה
                        "[NUM:min=0;max=1:*:#W]" +
                        "[IF:#W=0:<" +
                        "כמה שקלים סך הכל [#1:he_she] [VERB:id=need;t=past;g=(#1:g);num=s] [VERB:id=pay;f=inf]?" +
                        ">:<" +
                        "[#1:n] [VERB:id=pay;t=past;g=(#1:g);num=s] בשטר של [NUM:value=200:#Z] שקלים. " +
                        "כמה עודף [#1:he_she] [VERB:id=receive;t=past;g=(#1:g);num=s] מה[IF:#OG=0:<[#O1:m_s]>:<[#O1:f_s]>]?" +
                        ">]",

                // 4. תשובה
                "[IF:#W=0:<[NUM:value=(#X:mul_(#Y)):#R]>:<[NUM:value=(#Z:sub_(#X:mul_(#Y))):#R]>]"
        });

        // מכאן

        templates.add(new String[]{
                // 1. הגרלות נסתרות (מקום, מוכר, פריט שמוכרים בחנות הזו)
                "[NUM:min=0;max=1:*:#OG]" +
                        "[PLACE:place_type=STORE:*:#P1]" +
                        "[ROLE:place_id=(#P1:id);role_type=OPERATOR:*:#O1]" +
                        "[ITEM:type=(#P1:t):*:#2]" +

                        // 2. הסיפור
                        "[HUMAN:#1] [VERB:id=enter;t=past;g=(#1:g);num=s] ל[#P1:s]. " +
                        "ה[IF:(#OG)=0:<[#O1:m_s] תלה>:<[#O1:f_s] תלתה>] שלט מבצע על ה[#2:p]: " +
                        "\"קנו [NUM:min=3;max=5:#X] [UNIT:type=(#2:allowed_unit):p:#U1][IF:(#2:allowed_unit)=NONE:<>:< של>] [#2:p] [ADJ:id=new;g=(#2:g);num=p:#A1], " +
                        "וקבלו [NUM:min=1;max=2:#B] מתנה!\". " +

                        "[#1:n] [VERB:id=buy;t=past;g=(#1:g);num=s] [NUM:min=2;max=8:#Y] [IF:(#2:allowed_unit)=NONE:<[#2:p]>:<[#U1:p]>]. " +
                        // 3. השאלה (האם מגיע לו בונוס?)
                        "כמה [IF:(#2:allowed_unit)=NONE:<[#2:p]>:<[#U1:p]>] של [#2:p] יש ל[#1] עכשיו בסך הכל?",

                // 4. התשובה (מחשב בונוס רק אם הוא קנה מספיק)
                "[IF:(#Y)>=(#X):<[NUM:value=(#Y:add_(#B)):#R]>:<[NUM:value=(#Y):#R]>]"
        });


        templates.add(new String[]{
                // 1. הגרלות נסתרות
                "[PLACE:place_type=OUTDOORS|HOME:*:#P1]" +
                        "[ITEM:type=(#P1:t):*:#2]" +

                        // 2. הסיפור עם הפיצול הדינמי למקום
                        "[HUMAN:#1] ו[HUMAN:n=!(#1:n):#3] [IF:(#P1:pt)=OUTDOORS:<בילו ב[#P1:s] וחיפשו מציאות>:<[VERB:id=arrange;t=past;g=MALE;num=p] את ה[#P1:s]>]. " +

                        "[#1:n] [VERB:id=find;t=past;g=(#1:g);num=s] [NUM:min=10;max=25:#X] [#2:p] [ADJ:id=old;g=(#2:g);num=p:#A1]. " +
                        "[#3:n] [VERB:id=find;t=past;g=(#3:g);num=s] [NUM:min=10;max=25:#Y] [#2:p] [#A1]. " +

                        "לאחר מכן, הם החליטו לאסוף את הכל יחד ולחלק את כל מה שנמצא שווה בשווה ל-[NUM:min=3;max=6:#Z] קופסאות קטנות. " +

                        // 3. מגרילים איזה סוג שאלה נשאל (0 = שארית, 1 = כמות בכל קופסה)
                        "[NUM:min=0;max=1:*:#W]" +

                        // 4. השאלה - מפוצלת לפי W
                        "[IF:(#W)=0:<" +
                        "כמה [#2:p] [#A1] נשארו בחוץ כי לא התחלקו שווה בשווה בין הקופסאות?" +
                        ">:<" +
                        "כמה [#2:p] [#A1] נכנסו בדיוק לתוך כל קופסה?" +
                        ">]",

                // 5. התשובה - מפוצלת לפי W (חייבת להיות תואמת לשאלה!)
                "[IF:(#W)=0:<" +
                        "[NUM:value=(#X:add_(#Y)):mod_(#Z):#R]" + // חישוב שארית
                        ">:<" +
                        "[NUM:value=(#X:add_(#Y)):div_(#Z):#R]" + // חישוב חלוקה רגילה
                        ">]"
        });

        templates.add(new String[]{
                // 1. הגרלות נסתרות (הפריט מותאם דינמית למקום!)
                "[PLACE:place_type=EDUCATION|HOME:*:#P1]" +
                        "[ITEM:type=(#P1:t):*:#2]" +

                        // 2. הסיפור - נקי לגמרי מתנאי IF מסורבלים בזכות מילון הפעלים החדש
                        "[HUMAN:#1] [VERB:id=sit;t=past;g=(#1:g);num=s] ב[#P1:s] בשעה [TIME:min=14.00;max=16.00:#T1]. " +
                        "[#1:he_she] [VERB:id=can;t=present;g=(#1:g);num=s] [VERB:id=arrange;f=inf] [NUM:min=4;max=8:#X] [#2:p] בכל דקה. " +

                        // 3. הגרלת סוג השאלה: 0 = כמות פריטים, 1 = שעת סיום
                        "[NUM:min=0;max=1:*:#W]" +

                        // 4. השאלה המפוצלת (ללא אות ל' מיותרת לפני פועל המקור)
                        "[IF:(#W)=0:<" +
                        "אם [#1:he_she] [VERB:id=arrange;t=past;g=(#1:g);num=s] [#2:p] ברצף עד השעה [TIME:value=(#T1:add_m_15):#T2], כמה [#2:p] [#1:he_she] [VERB:id=arrange;t=past;g=(#1:g);num=s] סך הכל?" +
                        ">:<" +
                        "אם היו שם [NUM:value=(#X:mul_20):#Y] [#2:p] בסך הכל, באיזו שעה [#1:he_she] [VERB:id=finish;t=past;g=(#1:g);num=s] [VERB:id=arrange;f=inf] את כולם?" +
                        ">]",

                // 5. התשובה המפוצלת (מותאמת בדיוק לשאלה שנבחרה)
                "[IF:(#W)=0:<" +
                        "[NUM:value=(#X:mul_15):#R]" + // חישוב כמות: קצב * 15 דקות
                        ">:<" +
                        "[TIME:value=(#T1:add_m_20):#R]" + // חישוב שעה: מוסיף בדיוק 20 דקות לזמן ההתחלה
                        ">]"
        });

        List<String[]> newTemplates = new ArrayList<>();
//        newTemplates.add(new String[]{
//                "שאלה",
//                "תשובה",
//                "תשובה שגויה 1",
//                "תשובה שגויה 2",
//                "תשובה שגויה 2",
//                "רמז"
//        });

        // 9. תבנית קניות משותפות (חברים קונים יחד ומקבלים עודף או מוסיפים פריט משותף)
        newTemplates.add(new String[]{
                "[PLACE:place_type=FOOD_SERVICE|ENTERTAINMENT:*:#P1]" +
                        "[ITEM:type=(#P1:t);unit_type=COUNT:*:#I1]" +
                        "[ITEM:type=(#P1:t);unit_type=COUNT;id=!(#I1:id):*:#I2]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=3;max=6:*:#FRIENDS]" +
                        "[NUM:min=10;max=20:*:#P_PRICE]" +
                        "[NUM:min=15;max=35:*:#SHARED_PRICE]" +
                        "[NUM:value=(#FRIENDS:mul_(#P_PRICE)):*:#TOTAL_P]" +
                        "[NUM:value=(#TOTAL_P:add_(#SHARED_PRICE)):*:#TOTAL_COST]" +
                        "[NUM:value=200:*:#BILL]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=buy:(past_+(#1:g)+_s)] [#FRIENDS] [#I1:p] ב[#P1:s] לחברים. כל [#I1:s] עולה [#P_PRICE] שקלים. " +
                        "[IF:(#W)=0:<בנוסף, [#1:he_she] [VERB:id=buy:(past_+(#1:g)+_s)] גם [#I2:s] עבור כולם ב-[#SHARED_PRICE] שקלים. כמה שקלים סך הכל [#1:he_she] [VERB:id=pay:(past_+(#1:g)+_s)]?>" +
                        ":<בנוסף, [#1:he_she] [VERB:id=buy:(past_+(#1:g)+_s)] [#I2:s] ב-[#SHARED_PRICE] שקלים, ו[VERB:id=pay:(past_+(#1:g)+_s)] לקופאי בשטר של 200 שקלים. כמה עודף מגיע [#1:to_him_her]?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST):#R]>:<[NUM:value=(#BILL:sub_(#TOTAL_COST)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_P):#R]>:<[NUM:value=(#BILL:sub_(#TOTAL_P)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_COST:sub_10):#R]>:<[NUM:value=(#TOTAL_COST):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_P:add_(#FRIENDS)):#R]>:<[NUM:value=(#BILL:sub_(#SHARED_PRICE)):#R]>]",
                "[IF:(#W)=0:<חשב קודם את המחיר של כל [#I1:p] יחד (כפל), ואז הוסף את המחיר של [#I2:s].>:<חשב את העלות הכוללת (כפל ואז חיבור), וחסר את התוצאה מ-200.>]"
        });

        // 10. תבנית עבודה/סידור לפי קצב שעות (מטרה שבועית/יומית)
        newTemplates.add(new String[]{
                "[PLACE:place_type=EDUCATION|HOME:*:#P1]" +
                        "[ITEM:type=STATIONERY;unit_type=COUNT:*:#I1]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=6;max=12:*:#PER_HOUR]" +
                        "[NUM:min=3;max=6:*:#HOURS]" +
                        "[NUM:value=(#PER_HOUR:mul_(#HOURS)):*:#BASE_DONE]" +
                        "[NUM:min=15;max=30:*:#EXTRA]" +
                        "[NUM:value=(#BASE_DONE:add_(#EXTRA)):*:#TOTAL_GOAL]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=arrange:(present_+(#1:g)+_s)] [#I1:p] ב[#P1:s]. בכל שעה [#1:he_she] [VERB:id=arrange:(present_+(#1:g)+_s)] בדיוק [#PER_HOUR] [#I1:p]. " +
                        "[IF:(#W)=0:<אם [#1:he_she] [VERB:id=work:(past_+(#1:g)+_s)] במשך [#HOURS] שעות, ובערב [VERB:id=arrange:(past_+(#1:g)+_s)] עוד [#EXTRA] [#I1:p], כמה [#I1:p] [#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s)] סך הכל?>" +
                        ":<המטרה הייתה לסדר [#TOTAL_GOAL] [#I1:p]. אם [#1:he_she] [VERB:id=work:(past_+(#1:g)+_s)] רק במשך [#HOURS] שעות, כמה [#I1:p] עוד נשארו [#1:to_him_her] לסדר?>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE_DONE:add_(#EXTRA)):#R]>:<[NUM:value=(#TOTAL_GOAL:sub_(#BASE_DONE)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#BASE_DONE:sub_(#EXTRA)):abs:#R]>:<[NUM:value=(#BASE_DONE:add_(#EXTRA)):#R]>]",
                "[NUM:value=(#BASE_DONE):#R]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_GOAL:sub_10):#R]>:<[NUM:value=(#TOTAL_GOAL:sub_(#HOURS)):#R]>]",
                "[IF:(#W)=0:<הכפל את קצב הסידור במספר השעות, והוסף את הכמות של הערב.>:<הכפל את קצב הסידור במספר השעות, וחסר את התוצאה מתוך המטרה הכוללת.>]"
        });

// 11. תבנית שורות במערך (מטריצה) של פריטים + בלאי
        newTemplates.add(new String[]{
                "[PLACE:place_type=OUTDOORS|PUBLIC:*:#P1]" +
                        "[ITEM:type=(#P1:t):*:#I1]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=6;max=12:*:#ROWS]" +
                        "[NUM:min=8;max=15:*:#PER_ROW]" +
                        "[NUM:value=(#ROWS:mul_(#PER_ROW)):*:#TOTAL_ITEMS]" +
                        "[NUM:min=10;max=25:*:#BROKEN]" +
                        "[NUM:min=2;max=5:*:#EXTRA_ROWS]" +
                        "[NUM:value=(#EXTRA_ROWS:mul_(#PER_ROW)):*:#ADDED_ITEMS]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=arrange:(past_+(#1:g)+_s)] [IF:(#I1:allowed_unit)=NONE:<שורות של [#I1:p]>:<[UNIT:type=(#I1:allowed_unit);item_category=(#I1:t):p:#U1] של [#I1:p]>] ב[#P1:s]. " +
                        "[#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s)] [#ROWS] [IF:(#I1:allowed_unit)=NONE:<שורות>:<[#U1:p]>], ובכל [IF:(#I1:allowed_unit)=NONE:<שורה>:<[#U1:s]>] בדיוק [#PER_ROW] פריטים. " +
                        "[IF:(#W)=0:<לרוע המזל, [#BROKEN] פריטים נהרסו ונזרקו. כמה פריטים תקינים נשארו בסוף?>" +
                        ":<אחרי מנוחה, [#1:he_she] [VERB:id=add:(past_+(#1:g)+_s)] עוד [#EXTRA_ROWS] [IF:(#I1:allowed_unit)=NONE:<שורות מלאות>:<[#U1:p]>]. כמה פריטים יש עכשיו סך הכל?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_ITEMS:sub_(#BROKEN)):#R]>:<[NUM:value=(#TOTAL_ITEMS:add_(#ADDED_ITEMS)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_ITEMS:add_(#BROKEN)):#R]>:<[NUM:value=(#TOTAL_ITEMS:sub_(#ADDED_ITEMS)):#R]>]",
                "[NUM:value=(#TOTAL_ITEMS):#R]",
                "[IF:(#W)=0:<[NUM:value=(#ROWS:mul_(#BROKEN)):#R]>:<[NUM:value=(#TOTAL_ITEMS:add_(#EXTRA_ROWS)):#R]>]",
                "[IF:(#W)=0:<חשב כמה פריטים היו בהתחלה (כמות הקבוצות כפול כמות הפריטים בכל אחת), וחסר את אלו שנהרסו.>:<חשב כמה פריטים היו בהתחלה (כפל), והוסף את מספר הפריטים שנוספו לאחר מכן (כפל נוסף).>]"
        });

        // 12. תבנית מרחק הקפות אימון (מספרים עגולים גדולים יותר - כפל עשרות ומאות)
        newTemplates.add(new String[]{
                "[PLACE:place_type=OUTDOORS|PUBLIC:*:#P1]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=30;max=60;round=10:*:#LAP_LEN]" +
                        "[NUM:min=3;max=6:*:#LAPS]" +
                        "[NUM:value=(#LAP_LEN:mul_(#LAPS)):*:#TOTAL_DIST]" +
                        "[NUM:min=20;max=50;round=10:*:#REMAINING]" +
                        "[NUM:value=(#TOTAL_DIST:add_(#REMAINING)):*:#GOAL]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=run:(present_+(#1:g)+_s)] במסלול מעגלי ב[#P1:s]. אורך כל הקפה הוא [#LAP_LEN] מטרים. " +
                        "[IF:(#W)=0:<אם [#1:he_she] [VERB:id=run:(past_+(#1:g)+_s)] [#LAPS] הקפות, ונשארו [#1:to_him_her] עוד [#REMAINING] מטרים לסיום האימון, מה אורך האימון הכולל במטרים?>" +
                        ":<המטרה של [#1:n] היא לרוץ [#GOAL] מטרים. לאחר השלמת [#LAPS] הקפות מלאות, כמה מטרים עוד נשארו [#1:to_him_her] לרוץ?>]",
                "[IF:(#W)=0:<[NUM:value=(#GOAL):#R]>:<[NUM:value=(#REMAINING):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_DIST:sub_(#REMAINING)):#R]>:<[NUM:value=(#GOAL:add_(#TOTAL_DIST)):#R]>]",
                "[NUM:value=(#TOTAL_DIST):#R]",
                "[IF:(#W)=0:<[NUM:value=(#GOAL:sub_20):#R]>:<[NUM:value=(#REMAINING:add_20):#R]>]",
                "[IF:(#W)=0:<חשב את המרחק ש[#1:he_she] כבר [VERB:id=run:(past_+(#1:g)+_s)] (מספר הקפות כפול אורך הקפה), וחבר למרחק שנשאר.>:<חשב את המרחק ש[#1:he_she] כבר [VERB:id=run:(past_+(#1:g)+_s)] (כפל), וחסר אותו מתוך יעד האימון הכולל.>]"
        });

// 13. תבנית עומס ומשקלים (עגלות/משאיות)
        newTemplates.add(new String[]{
                "[PLACE:place_type=STORE|HOME:*:#P1]" +
                        "[ITEM:type=(#P1:t):*:#I1]" +
                        "[HUMAN:*:#1]" +
                        "[NUM:min=4;max=8:*:#BOXES]" +
                        "[NUM:min=12;max=25:*:#WEIGHT_PER]" +
                        "[NUM:value=(#BOXES:mul_(#WEIGHT_PER)):*:#TOTAL_WEIGHT]" +
                        "[NUM:min=35;max=60:*:#EXTRA_WEIGHT]" +
                        "[NUM:value=(#TOTAL_WEIGHT:add_(#EXTRA_WEIGHT)):*:#MAX_CAPACITY]" +
                        "[NUM:min=0;max=1:*:#W]" +
                        "[#1:n] [VERB:id=arrange:(past_+(#1:g)+_s)] [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[UNIT:type=(#I1:allowed_unit);item_category=(#I1:t):p:#U1] של [#I1:p]>] ב[#P1:s]. " +
                        "המשקל של כל [IF:(#I1:allowed_unit)=NONE:<[#I1:s]>:<[#U1:s]>] הוא בדיוק [#WEIGHT_PER] קילוגרמים. [#1:he_she] [VERB:id=arrange:(past_+(#1:g)+_s)] [#BOXES] [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[#U1:p]>] על העגלה. " +
                        "[IF:(#W)=0:<בנוסף, [#1:he_she] [VERB:id=add:(past_+(#1:g)+_s)] לעגלה פריט בודד ששוקל [#EXTRA_WEIGHT] ק\"ג. מה המשקל הכולל על העגלה כעת?>" +
                        ":<העגלה יכולה לשאת מקסימום [#MAX_CAPACITY] קילוגרמים. כמה קילוגרמים נוספים אפשר להעמיס עליה לפני שתשבר?>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_WEIGHT:add_(#EXTRA_WEIGHT)):#R]>:<[NUM:value=(#MAX_CAPACITY:sub_(#TOTAL_WEIGHT)):#R]>]",
                "[IF:(#W)=0:<[NUM:value=(#TOTAL_WEIGHT:sub_(#EXTRA_WEIGHT)):#R]>:<[NUM:value=(#MAX_CAPACITY:add_(#TOTAL_WEIGHT)):#R]>]",
                "[NUM:value=(#TOTAL_WEIGHT):#R]",
                "[IF:(#W)=0:<[NUM:value=(#MAX_CAPACITY:sub_10):#R]>:<[NUM:value=(#EXTRA_WEIGHT:add_10):#R]>]",
                "[IF:(#W)=0:<חשב את המשקל של [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[#U1:p]>] יחד (כפל), ואז הוסף את המשקל של הפריט הבודד.>:<חשב את המשקל של [IF:(#I1:allowed_unit)=NONE:<[#I1:p]>:<[#U1:p]>] שכבר על העגלה (כפל), וחסר אותו מהקיבולת המקסימלית.>]"
        });


        // הרצה של המנוע
        System.out.println("--- מריץ ייצור שאלות ---");

        for (int i = 0; i < newTemplates.size(); i++) {
            memory = new HashMap<>(); // זיכרון נקי לכל שאלה
            String[] pair = newTemplates.get(i);

            String question = questionEngine.evaluateTemplate(pair[0], memory);
            String answer = questionEngine.evaluateTemplate(pair[1], memory);
            String w1answer = questionEngine.evaluateTemplate(pair[2], memory);
            String w2answer = questionEngine.evaluateTemplate(pair[3], memory);
            String w3answer = questionEngine.evaluateTemplate(pair[4], memory);
            String hint = questionEngine.evaluateTemplate(pair[5], memory);

            System.out.println("שאלה " + (i+1) + ": " + question);
            System.out.println("תשובה נכונה: " + answer);
            System.out.println("תשובה שגויה 1 : " + w1answer);
            System.out.println("תשובה שגויה 2 : " + w2answer);
            System.out.println("תשובה שגויה 3 : " + w3answer);
            System.out.println("רמז : " + hint);
            System.out.println("-------------------------");
        }
    }
}
