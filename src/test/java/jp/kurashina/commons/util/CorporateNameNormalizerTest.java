package jp.kurashina.commons.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorporateNameNormalizerTest {

    @Test
    void expandsAbbreviations() {
        assertThat(CorporateNameNormalizer.normalizeCorporateName("（株）サンプル")).isEqualTo("株式会社サンプル");
        assertThat(CorporateNameNormalizer.normalizeCorporateName("サンプル(有)")).isEqualTo("サンプル有限会社");
        assertThat(CorporateNameNormalizer.normalizeCorporateName("㍿サンプル")).isEqualTo("株式会社サンプル");
        assertThat(CorporateNameNormalizer.normalizeCorporateName("(同)サンプル")).isEqualTo("合同会社サンプル");
    }

    @Test
    void removesWhitespaceAroundCorporateType() {
        assertThat(CorporateNameNormalizer.normalizeCorporateName("株式会社　サンプル")).isEqualTo("株式会社サンプル");
        assertThat(CorporateNameNormalizer.normalizeCorporateName(" サンプル 株式会社 ")).isEqualTo("サンプル株式会社");
        assertThat(CorporateNameNormalizer.normalizeCorporateName("（株） サンプル")).isEqualTo("株式会社サンプル");
    }

    @Test
    void keepsWhitespaceThatIsNotNextToCorporateType() {
        assertThat(CorporateNameNormalizer.normalizeCorporateName("株式会社サンプル 東京支店")).isEqualTo("株式会社サンプル 東京支店");
    }

    @Test
    void returnsNullOrBlankInputUnchanged() {
        assertThat(CorporateNameNormalizer.normalizeCorporateName(null)).isNull();
        assertThat(CorporateNameNormalizer.normalizeCorporateName("")).isEmpty();
        assertThat(CorporateNameNormalizer.normalizeCorporateName("   ")).isEqualTo("   ");
    }

    @Test
    void retrieveDisplayNameRemovesCorporateType() {
        assertThat(CorporateNameNormalizer.retrieveDisplayName("（株）サンプル")).isEqualTo("サンプル");
        assertThat(CorporateNameNormalizer.retrieveDisplayName("一般社団法人 サンプル協会")).isEqualTo("サンプル協会");
        assertThat(CorporateNameNormalizer.retrieveDisplayName("サンプル")).isEqualTo("サンプル");
    }

    @Test
    void prefersLongerCorporateTypeThatContainsShorterOne() {
        assertThat(CorporateNameNormalizer.retrieveDisplayName("医療法人社団健康会")).isEqualTo("健康会");
        assertThat(CorporateNameNormalizer.retrieveDisplayName("特定医療法人サンプル会")).isEqualTo("サンプル会");
        assertThat(CorporateNameNormalizer.retrieveDisplayName("社会医療法人サンプル会")).isEqualTo("サンプル会");
        assertThat(CorporateNameNormalizer.retrieveDisplayName("医療法人サンプル会")).isEqualTo("サンプル会");
        assertThat(CorporateNameNormalizer.retrieveDisplayName("地方独立行政法人サンプル病院")).isEqualTo("サンプル病院");
        assertThat(CorporateNameNormalizer.retrieveDisplayName("公益財団法人サンプル財団")).isEqualTo("サンプル財団");
    }

    @Test
    void removesWhitespaceAfterLongerCorporateType() {
        assertThat(CorporateNameNormalizer.normalizeCorporateName("医療法人社団 健康会")).isEqualTo("医療法人社団健康会");
        assertThat(CorporateNameNormalizer.normalizeCorporateName("特定医療法人　サンプル会")).isEqualTo("特定医療法人サンプル会");
        assertThat(CorporateNameNormalizer.normalizeCorporateName("サンプル 協同組合連合会")).isEqualTo("サンプル協同組合連合会");
    }

    @Test
    void exposesCorporateTypesInOriginalOrder() {
        assertThat(new CorporateNameNormalizer().getCorporateTypes()).startsWith("株式会社", "相互会社", "有限会社");
    }

    @Test
    void exposesCorporateTypes() {
        assertThat(new CorporateNameNormalizer().getCorporateTypes()).contains("株式会社", "社会福祉法人", "NPO法人");
    }
}
