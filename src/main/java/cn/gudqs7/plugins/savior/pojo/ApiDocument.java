package cn.gudqs7.plugins.savior.pojo;

import cn.gudqs7.plugins.common.pojo.resolver.ResponseCodeInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/** Pure template model for a generated API document. */
@Data
public class ApiDocument {

    private String interfaceName;
    private String interfaceNotes;
    private String qualifiedMethodName;
    private String url;
    private String method;
    private String contentType;
    private Map<String, List<FieldLevelInfo>> paramLevelMap;
    private Map<String, List<FieldLevelInfo>> returnLevelMap;
    private String jsonExample;
    private String returnJsonExample;
    private List<ResponseCodeInfo> responseCodeInfoList;
}
