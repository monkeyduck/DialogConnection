package com.analysis;

import com.http.HttpTookit;
import com.mvc.service.ServiceHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by llc on 2017/6/2.
 */
public class LogInterface {
    private static String address = "http://stat.hixiaole.com:8081/";

//    @Autowired
//    private NodeService nodeService;
//
//    public void setNodeService(NodeService nodeService) {
//        this.nodeService = nodeService;
//    }

//    private static HttpToolkit httpToolkit = new HttpToolkit();
    public void searchLog(String date) {
        String path = "log/search/date?date=" + date + "&module=dialogtree&src=xiaole";
//        String path = "log/search/latestChatLog?count=100";
        String result = HttpTookit.get(address + path);
//        String result = "[{\"packageName\":\"com.beva.bevatingting\",\"deviceId\":\"00:16:6d:30:f7:aa\",\"environment\":\"release\",\"ip\":\"10.45.146.36\",\"level\":\"INFO\",\"memberId\":\"sT70og7ZSJ2PHqSUvDiCeg==\",\"module\":\"dialogtree\",\"timeStamp\":\"1496275538222\",\"content\":\"{\\\"userId\\\":\\\"sT70og7ZSJ2PHqSUvDiCeg==\\\",\\\"treeId\\\":\\\"1188\\\",\\\"timestamp\\\":1496275538219,\\\"nodeId\\\":\\\"22752\\\",\\\"methodName\\\":\\\"nodeEnterIn\\\"}\",\"@version\":\"1\",\"@timestamp\":\"2017-06-01T08:05:38.222+08:00\",\"path\":\"/root/wenshiyang/dialogtree_release/stats_logs/2017-06-01.log\",\"host\":\"iZ25e40l3kyZ\",\"type\":\"stats\",\"debug\":\"timestampMatched\",\"hdfs_filename\":\"2017-06-01\",\"redis_key\":\"2017-06-01.08:05\"},{\"timeStamp\":1496275538060,\"environment\":\"release\",\"level\":\"INFO\",\"module\":\"cockroach\",\"ip\":\"127.0.0.1\",\"from\":\"controller\",\"to\":\"dialog\",\"packageName\":\"com.beva.bevatingting\",\"deviceId\":\"00:16:6d:30:f7:aa\",\"content\":\"{\\\"packageMeta\\\":{\\\"extraMap\\\":{\\\"secretKey\\\":\\\"PFwe+yPKnohMwOJgHtx2lJhxhaqZdrIvDRPFeflBw74\\\\u003d\\\",\\\"signature\\\":\\\"E5:C7:BE:46:5C:5F:89:A6:E3:CF:D1:B7:09:65:7E:F7:FB:23:F7:D9\\\",\\\"packageName\\\":\\\"com.beva.bevatingting\\\",\\\"action_after_execution\\\":\\\"continue_listen\\\"},\\\"clientVersion\\\":\\\"v1.0.1\\\",\\\"clientType\\\":\\\"robot\\\",\\\"clientId\\\":\\\"GID_XIAOLE_R@@@00:16:6d:30:f7:aa\\\",\\\"deviceId\\\":\\\"00:16:6d:30:f7:aa\\\",\\\"protocolVersion\\\":\\\"v1.0\\\",\\\"packageUUID\\\":\\\"4dc1caa7-95f1-4137-acdc-4ec7a2f2d3ee\\\",\\\"priority\\\":1,\\\"createdTimestamp\\\":1496275538052,\\\"packageType\\\":\\\"common\\\",\\\"userId\\\":\\\"sT70og7ZSJ2PHqSUvDiCeg\\\\u003d\\\\u003d\\\"},\\\"from\\\":{\\\"name\\\":\\\"controller\\\"},\\\"to\\\":{\\\"name\\\":\\\"dialog\\\"},\\\"packageContentList\\\":[{\\\"map\\\":{\\\"msg_content\\\":\\\"{\\\\\\\"ch_rec_list\\\\\\\":[{\\\\\\\"content\\\\\\\":\\\\\\\"是呀\\\\\\\"}]}\\\",\\\"msg_type\\\":\\\"text\\\"}}]}\",\"memberId\":\"sT70og7ZSJ2PHqSUvDiCeg==\",\"@version\":\"1\",\"@timestamp\":\"2017-06-01T08:05:38.060+08:00\",\"path\":\"/root/wangzhiwei/cockroach/stats_logs/2017-06-01.log\",\"host\":\"iZ25841et8bZ\",\"type\":\"stats\",\"debug\":\"timestampMatched\",\"hdfs_filename\":\"2017-06-01\",\"redis_key\":\"2017-06-01.08:05\"}]";
        JSONArray jsonArray = JSONArray.fromObject(result);
        Iterator<JSONObject> iterator = jsonArray.iterator();
        Map<String, List<String>> idMap = new HashMap<String, List<String>>();
        while (iterator.hasNext()) {
            JSONObject json = iterator.next();
            String memberId = json.getString("memberId");
            if (checkMemberId(memberId)) {
                if (!idMap.containsKey(memberId)) {
                    idMap.put(memberId, new ArrayList<String>());
                }
                idMap.get(memberId).add(json.toString());
            }
        }
        for (String memberId: idMap.keySet()) {
            System.out.println("member id: " + memberId);
            checkElse(memberId, idMap.get(memberId));
//            for (String slog: idMap.get(memberId)) {
//                System.out.println(slog);
//            }
            System.out.println("================\r\n");
        }
    }

    private boolean checkMemberId(String memberId) {
        return memberId.endsWith("==") || (memberId.contains(".") && memberId.length() >= 32);
    }

    public void checkElse(String memberId, List<String> logList) {
        int count = 0;
        for (String slog: logList) {
            JSONObject json = JSONObject.fromObject(slog);
            try {
                JSONObject jsonContent = json.getJSONObject("content");
                if (jsonContent.containsKey("nodeId")) {
                    String nodeId = jsonContent.getString("nodeId");
                    int inodeId = Integer.parseInt(nodeId);
                    int rootId = ServiceHelper.getNodeService().getRootIdByTopicId(479);
                    System.out.println(rootId);
                    System.out.println("NodeId: " + nodeId);
                    String nodeContent = ServiceHelper.getNodeService().getNodeContent(inodeId);
                    System.out.println("Nodecontent:" + nodeContent);
                    if (nodeContent.contains("else")) {
                        count++;
                        if (count >= 3) {
                            System.out.println(memberId + "连续三次else");
                        }
                    } else {
                        count = 0;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(slog);
            }

        }
    }

    public static void main(String[] args) {
        LogInterface logInterface = new LogInterface();
        logInterface.searchLog("2017-06-12");
    }
}
