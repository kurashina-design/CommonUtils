package jp.kurashina.commons.util;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PrefectureUtils {

    private static final Map<String, String> prefectureMap = new HashMap<>();
    private static final Map<String, String> prefectureReverseMap = new HashMap<>();

    public static String getPrefectureCode(String prefectureName) {
        if (MapUtils.isEmpty(prefectureMap)) {
            prefectureMap.put("北海道", "01");
            prefectureMap.put("青森県", "02");
            prefectureMap.put("岩手県", "03");
            prefectureMap.put("宮城県", "04");
            prefectureMap.put("秋田県", "05");
            prefectureMap.put("山形県", "06");
            prefectureMap.put("福島県", "07");
            prefectureMap.put("茨城県", "08");
            prefectureMap.put("栃木県", "09");
            prefectureMap.put("群馬県", "10");
            prefectureMap.put("埼玉県", "11");
            prefectureMap.put("千葉県", "12");
            prefectureMap.put("東京都", "13");
            prefectureMap.put("神奈川県", "14");
            prefectureMap.put("新潟県", "15");
            prefectureMap.put("富山県", "16");
            prefectureMap.put("石川県", "17");
            prefectureMap.put("福井県", "18");
            prefectureMap.put("山梨県", "19");
            prefectureMap.put("長野県", "20");
            prefectureMap.put("岐阜県", "21");
            prefectureMap.put("静岡県", "22");
            prefectureMap.put("愛知県", "23");
            prefectureMap.put("三重県", "24");
            prefectureMap.put("滋賀県", "25");
            prefectureMap.put("京都府", "26");
            prefectureMap.put("大阪府", "27");
            prefectureMap.put("兵庫県", "28");
            prefectureMap.put("奈良県", "29");
            prefectureMap.put("和歌山県", "30");
            prefectureMap.put("鳥取県", "31");
            prefectureMap.put("島根県", "32");
            prefectureMap.put("岡山県", "33");
            prefectureMap.put("広島県", "34");
            prefectureMap.put("山口県", "35");
            prefectureMap.put("徳島県", "36");
            prefectureMap.put("香川県", "37");
            prefectureMap.put("愛媛県", "38");
            prefectureMap.put("高知県", "39");
            prefectureMap.put("福岡県", "40");
            prefectureMap.put("佐賀県", "41");
            prefectureMap.put("長崎県", "42");
            prefectureMap.put("熊本県", "43");
            prefectureMap.put("大分県", "44");
            prefectureMap.put("宮崎県", "45");
            prefectureMap.put("鹿児島県", "46");
            prefectureMap.put("沖縄県", "47");
        }
        return prefectureMap.get(prefectureName);
    }

    public static String getPrefectureName(String prefectureCode) {
        if (prefectureCode.length() == 1) {
            prefectureCode = "0" + prefectureCode;
        }
        if (MapUtils.isEmpty(prefectureReverseMap)) {
            prefectureReverseMap.put("01", "北海道");
            prefectureReverseMap.put("02", "青森県");
            prefectureReverseMap.put("03", "岩手県");
            prefectureReverseMap.put("04", "宮城県");
            prefectureReverseMap.put("05", "秋田県");
            prefectureReverseMap.put("06", "山形県");
            prefectureReverseMap.put("07", "福島県");
            prefectureReverseMap.put("08", "茨城県");
            prefectureReverseMap.put("09", "栃木県");
            prefectureReverseMap.put("10", "群馬県");
            prefectureReverseMap.put("11", "埼玉県");
            prefectureReverseMap.put("12", "千葉県");
            prefectureReverseMap.put("13", "東京都");
            prefectureReverseMap.put("14", "神奈川県");
            prefectureReverseMap.put("15", "新潟県");
            prefectureReverseMap.put("16", "富山県");
            prefectureReverseMap.put("17", "石川県");
            prefectureReverseMap.put("18", "福井県");
            prefectureReverseMap.put("19", "山梨県");
            prefectureReverseMap.put("20", "長野県");
            prefectureReverseMap.put("21", "岐阜県");
            prefectureReverseMap.put("22", "静岡県");
            prefectureReverseMap.put("23", "愛知県");
            prefectureReverseMap.put("24", "三重県");
            prefectureReverseMap.put("25", "滋賀県");
            prefectureReverseMap.put("26", "京都府");
            prefectureReverseMap.put("27", "大阪府");
            prefectureReverseMap.put("28", "兵庫県");
            prefectureReverseMap.put("29", "奈良県");
            prefectureReverseMap.put("30", "和歌山県");
            prefectureReverseMap.put("31", "鳥取県");
            prefectureReverseMap.put("32", "島根県");
            prefectureReverseMap.put("33", "岡山県");
            prefectureReverseMap.put("34", "広島県");
            prefectureReverseMap.put("35", "山口県");
            prefectureReverseMap.put("36", "徳島県");
            prefectureReverseMap.put("37", "香川県");
            prefectureReverseMap.put("38", "愛媛県");
            prefectureReverseMap.put("39", "高知県");
            prefectureReverseMap.put("40", "福岡県");
            prefectureReverseMap.put("41", "佐賀県");
            prefectureReverseMap.put("42", "長崎県");
            prefectureReverseMap.put("43", "熊本県");
            prefectureReverseMap.put("44", "大分県");
            prefectureReverseMap.put("45", "宮崎県");
            prefectureReverseMap.put("46", "鹿児島県");
            prefectureReverseMap.put("47", "沖縄県");
        }
        return prefectureReverseMap.get(prefectureCode);
    }

}
