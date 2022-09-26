package cn.gudqs7.plugins.savior.theme;

/**
 * @author WQ
 */
public class ThemeHelper {

    public static Theme getRpcTheme() {
        return RpcTheme.getInstance();
    }

    public static Theme getRestfulTheme() {
        return RestfulTheme.getInstance();
    }

    public static Theme getJsonRpcTheme() {
        return JsonRpcTheme.getInstance();
    }

}
