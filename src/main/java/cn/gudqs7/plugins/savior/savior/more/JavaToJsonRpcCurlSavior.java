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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import org.apache.commons.collections.CollectionUtils;

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
        Boolean onlyRequire = (Boolean) param.get("onlyRequire");
        // POST + requestBody
        String raw = getRaw(paramStructureAndCommentInfo, onlyRequire);
        String jsonRpcRawBody = String.format(JSON_RPC_2_0_TEMPLATE, publicMethod.getNameIdentifier().getText(), raw);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(jsonRpcRawBody);
        jsonRpcRawBody =  gson.toJson(je);
        return String.format("curl --location --request POST '%s' \\ \n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data-raw '%s'", url, jsonRpcRawBody);
    }

    public static final String JSON_RPC_2_0_TEMPLATE = "{\n" +
            "    \"method\": \"%s\",\n" +
            "    \"jsonrpc\": \"2.0\",\n" +
            "    \"params\": \n" +
            "        %s\n" +
            "    ,\n" +
            "    \"id\": 0\n" +
            "}";

    private String getRaw(StructureAndCommentInfo paramStructureAndCommentInfo, Boolean onlyRequire) {
        HashMap<String, Object> data = new HashMap<>(2);
        data.put("onlyRequire", onlyRequire);
        Map<String, Object> java2jsonMap = java2JsonReader.read(paramStructureAndCommentInfo, data);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode paramsNode = new ArrayNode(objectMapper.getNodeFactory());
        java2jsonMap.forEach((key, value) -> {
            JsonNode argNode = objectMapper.valueToTree(value);
            paramsNode.add(argNode);
        });
        return paramsNode.toString();
    }
}
