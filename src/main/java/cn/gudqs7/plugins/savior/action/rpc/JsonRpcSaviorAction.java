package cn.gudqs7.plugins.savior.action.rpc;

import cn.gudqs7.plugins.savior.action.base.AbstractDocerSavior;
import cn.gudqs7.plugins.savior.savior.more.JavaToJsonRpcCurlSavior;
import cn.gudqs7.plugins.savior.theme.ThemeHelper;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

/**
 * @author 赵旭阳
 * @BelongProjecet docer-savior-idea-plugin
 * @BelongPackage cn.gudqs7.plugins.savior.action.rpc
 * @Copyleft 2013-3102
 * @Date 2022/9/23 16:42
 * @Description
 */

public class JsonRpcSaviorAction extends AbstractDocerSavior {
    private JavaToJsonRpcCurlSavior javaToJsonRpcCurlSavior = new JavaToJsonRpcCurlSavior(ThemeHelper.getJsonRpcTheme());

    public JsonRpcSaviorAction() {
        super(ThemeHelper.getJsonRpcTheme());
    }

    @Override
    protected String handlePsiMethod0(Project project, PsiMethod psiMethod, String psiClassName) {
        return javaToJsonRpcCurlSavior.generateJsonRpcCurl(project, psiClassName, psiMethod, false);
    }
}
