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
    void exposesCorporateTypes() {
        assertThat(new CorporateNameNormalizer().getCorporateTypes()).contains("株式会社", "社会福祉法人", "NPO法人");
    }
}
