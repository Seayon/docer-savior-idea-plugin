package cn.gudqs7.plugins.savior.savior.more;

import cn.gudqs7.plugins.common.consts.MapKeyConstant;
import cn.gudqs7.plugins.common.pojo.resolver.CommentInfo;
import cn.gudqs7.plugins.common.pojo.resolver.RequestMapping;
import cn.gudqs7.plugins.common.pojo.resolver.StructureAndCommentInfo;
import cn.gudqs7.plugins.common.util.JsonUtil;
import cn.gudqs7.plugins.savior.pojo.PostmanKvInfo;
import cn.gudqs7.plugins.savior.reader.Java2BulkReader;
import cn.gudqs7.plugins.savior.savior.base.AbstractSavior;
import cn.gudqs7.plugins.savior.theme.Theme;
import cn.gudqs7.plugins.savior.util.RestfulUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 赵旭阳
 * @BelongProjecet docer-savior-idea-plugin
 * @BelongPackage cn.gudqs7.plugins.savior.savior.more
 * @Copyleft 2013-3102
 * @Date 2022/9/23 16:58
 * @Description
 */

public class JavaToJsonRpcCurlSavior extends AbstractSavior<String> {
    private final Java2BulkReader java2BulkReader;

    public JavaToJsonRpcCurlSavior(Theme theme) {
        super(theme);
        java2BulkReader = new Java2BulkReader(theme);
    }


    public String generateJsonRpcCurl(Project project, String interfaceClassName, PsiMethod publicMethod, boolean onlyRequire) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("onlyRequire", onlyRequire);
        String curl = getDataByMethod(project, interfaceClassName, publicMethod, param, true);
        if (curl == null) {
            return "";
        }
        return curl;
    }

    @Override
    protected String getDataByStructureAndCommentInfo(Project project, PsiMethod publicMethod, CommentInfo commentInfo, String interfaceClassName, StructureAndCommentInfo paramStructureAndCommentInfo, StructureAndCommentInfo returnStructureAndCommentInfo, Map<String, Object> param) {
        String url = commentInfo.getUrl("");
        String contentType = commentInfo.getContentType(theme.getDefaultContentType());
        String method = commentInfo.getMethod("");
        String method0 = RestfulUtil.getFirstMethod(method);
        boolean firstMethodIsGet = RequestMapping.Method.GET.equals(method0);

        HashMap<String, Object> data = new HashMap<>(2);
        data.put("removeRequestBody", true);
        List<PostmanKvInfo> queryList = java2BulkReader.read(paramStructureAndCommentInfo, data);
        List<PostmanKvInfo> kvList = java2BulkReader.read(paramStructureAndCommentInfo);

        Boolean onlyRequire = (Boolean) param.get("onlyRequire");

        String query = RestfulUtil.getUrlQuery(queryList, onlyRequire);

        String curl = "";
        if (firstMethodIsGet) {
            // 纯 GET, 参数拼接到 url 后面
            curl = String.format("curl --location --request GET '%s'", url + query);
        } else {
            switch (contentType) {
                case RequestMapping.ContentType.APPLICATION_JSON:
                    // POST + requestBody
                    String raw = getRaw(paramStructureAndCommentInfo, onlyRequire);
                    curl = String.format("curl --location --request POST '%s' --header 'Content-Type: application/json' --data-raw '%s'", url + query, raw);
            }
        }
        return curl;
    }

    private String getRaw(StructureAndCommentInfo paramStructureAndCommentInfo, Boolean onlyRequire) {
        HashMap<String, Object> data = new HashMap<>(2);
        data.put("onlyRequire", onlyRequire);
        Map<String, Object> java2jsonMap = java2JsonReader.read(paramStructureAndCommentInfo, data);
        Object key = java2jsonMap.get(MapKeyConstant.HAS_REQUEST_BODY);
        if (key instanceof String) {
            String key0 = (String) key;
            return JsonUtil.toJson(java2jsonMap.get(key0));
        }
        return null;
    }
}
