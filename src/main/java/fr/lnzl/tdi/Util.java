package fr.lnzl.tdi;

import java.util.Random;

public class Util {

    public static float getRedFromColor(int color) {
        return ((color >> 16) & 0xff) / 255F;
    }

    public static float getGreenFromColor(int color) {
        return ((color >> 8) & 0xff) / 255F;
    }

    public static float getBlueFromColor(int color) {
        return ((color) & 0xff) / 255F;
    }

    public static float getAlphaFromColor(int color) {
        return ((color >> 24) & 0xff) / 255F;
    }

    public static int getColorFromRGBA(float red, float green, float blue, float alpha) {
        return ((int) (alpha * 255) << 24) |
                ((int) (red * 255) << 16) |
                ((int) (green * 255) << 8) |
                (int) (blue * 255);
    }

    public static String formatDamageText(float amount) {
        if (amount % 1.0 == 0) {
            return String.format("%.0f", amount);
        } else {
            return String.format("%.1f", amount);
        }
    }

    public static String fetchOnomatopoeia() {
        String[] onoStrings = {
                "AIEEE!", "AIIEEE!", "ARRGH!", "AWK!", "AWKKKKKK!", "BAM!", "BANG!", "BANG-ETH!", "BIFF!", "BLOOP!",
                "BLURP!", "BOFF!", "BONK!", "CLANK!", "CLANK-EST!", "CLASH!", "CLUNK!", "CLUNK-ETH!", "CRRAACK!",
                "CRASH!", "CRRAACK!", "CRUNCH!", "CRUNCH-ETH!", "EEE-YOW!", "FLRBBBBB!", "GLIPP!", "GLURPP!", "KAPOW!",
                "KAYO!", "KER-SPLOOSH!", "KERPLOP!", "KLONK!", "KLUNK!", "KRUNCH!", "OOOFF!", "OOOOFF!", "OUCH!",
                "OUCH-ETH!", "OWWW!", "OW-ETH", "PAM!", "PLOP!", "POW!", "POWIE!", "QUNCKKK!", "RAKKK!", "RIP!",
                "SLOSH!", "SOCK!", "SPLATS!", "SPLATT!", "SPLOOSH!", "SWAAP!", "SWISH!", "SWOOSH!", "THUNK!", "THWACK!",
                "THWACKE!", "THWAPE!", "THWAPP!", "UGGH!", "URKKK!", "VRONK!", "WHACK!", "WHACK-ETH!", "WHAM-ETH!",
                "WHAMM!", "WHAMMM!", "WHAP!", "Z-ZWAP!", "ZAM!", "ZAMM!", "ZAMMM!", "ZAP!", "ZAP-ETH", "ZGRUPPP!",
                "ZLONK!", "ZLOPP!", "ZLOTT!", "ZOK!", "ZOWIE!", "ZWAPP!", "ZZWAP!", "ZZZZWAP!", "ZZZZZWAP!"
        };
        return onoStrings[new Random().nextInt(onoStrings.length)];
    }
}