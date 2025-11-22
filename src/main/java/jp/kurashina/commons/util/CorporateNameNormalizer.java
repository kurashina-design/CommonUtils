package jp.kurashina.commons.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class CorporateNameNormalizer {

    private static final Map<String, String> ABBREVIATION_MAP = Map.ofEntries(
            entry("（株）", "株式会社"),
            entry("(株)", "株式会社"),
            entry("㍿", "株式会社"),
            entry("㏍", "株式会社"),
            entry("（有）", "有限会社"),
            entry("(有)", "有限会社"),
            entry("（同）", "合同会社"),
            entry("(同)", "合同会社"),
            entry("（名）", "合名会社"),
            entry("(名)", "合名会社"),
            entry("（資）", "合資会社"),
            entry("(資)", "合資会社")
    );

    private static final Pattern ABBREVIATION_REGEX = Pattern.compile(
            ABBREVIATION_MAP.keySet().stream()
                    .map(Pattern::quote)
                    .collect(Collectors.joining("|"))
    );

    // --- 空白除去のための定数 (前回の回答から) ---
    private static final List<String> CORPORATE_TYPES = Arrays.asList(
            "株式会社", "相互会社", "有限会社", "合同会社", "合名会社", "合資会社",
            "一般社団法人", "公益社団法人", "一般財団法人", "公益財団法人", "財団法人", "社団法人",
            "社会福祉法人", "社会福祉協議会",
            "医療法人", "特定医療法人", "医療法人社団", "社会医療法人",
            "NPO法人", "特定非営利活動法人", "宗教法人", "学校法人",
            "独立行政法人", "地方独立行政法人", "国立大学法人", "公立大学法人",
            "生活協同組合", "企業組合", "農業協同組合", "漁業協同組合", "森林組合", "事業協同組合", "協業組合", "協同組合連合会",
            "農業組合法人", "農業生産法人", "農地所有適格法人",
            "信用金庫", "監査法人", "弁護士法人", "税理士法人", "司法書士法人",
            "管理組合法人"
    );

    private static final Pattern WHITESPACE_PATTERN;

    static {
        String typesRegex = CORPORATE_TYPES.stream()
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));

        // \s* は半角・全角スペースを含むあらゆる空白文字にマッチします。
        WHITESPACE_PATTERN = Pattern.compile(String.format("(\\s*)(%s)(\\s*)", typesRegex));
    }


    // ------------------------------------------
    //             統合されたメソッド
    // ------------------------------------------

    /**
     * 会社名に含まれる法人格の略称を正式名称に正規化し、法人格前後の不要な空白を除去します。
     * * @param corporateName 略称や空白を含む可能性のある会社名
     * @return 略称が正式名称に、空白が除去された正規化済みの会社名
     */
    public static String normalizeCorporateName(String corporateName) {
        if (corporateName == null || corporateName.trim().isEmpty()) {
            return corporateName;
        }

        // 1. 略称を正式名称へ置換 (例: (株) -> 株式会社)
        String step1Result = ABBREVIATION_REGEX.matcher(corporateName).replaceAll(match -> {
            String abbreviation = match.group();
            return ABBREVIATION_MAP.get(abbreviation);
        });

        // 2. 法人格前後の不要な空白を除去 (例: 株式会社　ニシケン -> 株式会社ニシケン)
        String finalResult = WHITESPACE_PATTERN.matcher(step1Result).replaceAll(match -> {
            // グループ2 (法人格の本体) のみ残し、前後の空白(グループ1, 3)を削除
            return match.group(2);
        });

        // 最後に、文字列全体の前後にある空白を念のため除去
        return finalResult.trim();
    }

    public List<String> getCorporateTypes() {
        return CORPORATE_TYPES;
    }
}