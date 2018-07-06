package com.fstar.cms;

import com.fstar.sys.DB;
import com.fstar.utility.Machine;
import com.fstar.utility.XmlConverUtil;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

@WebServlet("/WXBO")
public class WXBO  extends HttpServlet {

    // 统一下单：
    public Map<String, Object> get_code_url(Map<String, Object> map)
            throws Exception {
        Map<String, Object> returnmap = new HashMap<String, Object>();

        try {
            WXConfig config = new WXConfig();
            WXPay wxpay = new WXPay(config);

            String product_id = (String) map.get("product_id");

            if (product_id != null && product_id.startsWith("VIP")){
                List<Map<String, Object>> datas = DB.seleteByKey("fs_product", map);
                if (datas.size() == 1){
                    Map<String, Object> product = datas.get(0);
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("out_trade_no", UUID.randomUUID().toString().replace("-",""));
                    data.put("fee_type", "CNY");
                    data.put("trade_type", "NATIVE");  // 此处指定为扫码支付
                    data.put("product_id", product_id);
                    data.put("device_info", (String) map.get("device_info"));
                    data.put("attach", (String) product.get("attach"));
                    data.put("total_fee", String.valueOf((Integer) product.get("total_fee")));
                    data.put("body", (String) product.get("body"));
                    data.put("time_start", Machine.formatdatetimeWX.format(new Date()));

                    //参数服务器终端IP
                    data.put("spbill_create_ip", "121.40.112.83");

                    //通知地址
                    data.put("notify_url", "http://121.40.112.83:8085/FStarWeb/WXBO");

                    Map<String, String> resp = wxpay.unifiedOrder(data);
                    System.out.println(resp);
                    if (resp.get("code_url") != null){
                        returnmap.put("out_trade_no",data.get("out_trade_no"));
                        returnmap.put("code_url",resp.get("code_url"));
                        //记录临时订单
                        Map<String, Object> recordData = new HashMap<String, Object>();
                        recordData.putAll(data);
                        recordData.putAll(resp);
                        DB.insert("fs_deal", recordData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnmap;
    }

    public Map<String, Object> get_deal(Map<String, Object> map)
            throws Exception {
        Map<String, Object> returnmap = new HashMap<String, Object>();
        List<Map<String, Object>> datas = DB.seleteByKey("fs_deal", map);
        if (datas.size() == 1) {
            returnmap = datas.get(0);
        }
        return returnmap;
    }

    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            InputStream inStream = req.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String result = new String(outSteam.toByteArray(), "utf-8");
            Map<String, String> data = XmlConverUtil.wxXmltoMap(result);

            System.out.println("WX NOTICE: " + result);

            // 此处调用订单查询接口验证是否交易成功
            boolean isSucc = reqOrderQueryResult(data.get("out_trade_no"));

            // 支付成功，商户处理后同步返回给微信参数
            PrintWriter writer = res.getWriter();
            if (!isSucc) {
                // 支付失败， 记录流水失败
                System.out.println("===============支付失败==============");
            } else {

                //更新交易
                Map<String, Object> recordData = new HashMap<String, Object>();
                recordData.putAll(data);
                DB.update("fs_deal", recordData);

                //记录VIP用户
                recordData.put("validity", Machine.getDateAddByMonth(Integer.parseInt((String) data.get("attach"))));
                recordData.put("remark", "VIP付费月数:"+data.get("attach"));
                DB.update("fs_terminal", recordData);

                System.out.println("===============付款成功，业务处理完毕==============");

                // 通知微信已经收到消息，不要再给我发消息了，否则微信会8连击调用本接口
                String noticeStr = setXML("SUCCESS", "");
                writer.write(noticeStr);
                writer.flush();
                return;
            }

            String noticeStr = setXML("FAIL", "");
            writer.write(noticeStr);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String setXML(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";
    }

    /**
     * 目前用的这个接口
     * @Description: 查询通知的结果bean
     * @param out_trade_no
     * @return
     *
     * @author leechenxiang
     * @date 2016年12月8日 上午11:04:52
     */
    public boolean reqOrderQueryResult(String out_trade_no) throws Exception {
        WXConfig config = new WXConfig();
        WXPay wxpay = new WXPay(config);
        Map<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", out_trade_no);
        Map<String, String> orderMap = wxpay.orderQuery(data);
        //此处添加支付成功后，支付金额和实际订单金额是否等价，防止钓鱼
        if (orderMap.get("return_code") != null && orderMap.get("return_code").equalsIgnoreCase("SUCCESS")) {
            if (orderMap.get("trade_state") != null && orderMap.get("trade_state").equalsIgnoreCase("SUCCESS")) {
                // 查询订单（交易流水的实际金额），判断微信收到的钱和订单中的钱是否等额
                Map<String, Object> key = new HashMap<String, Object>();
                key.put("out_trade_no",out_trade_no);
                List<Map<String, Object>> datas = DB.seleteByKey("fs_deal", key);
                if (datas.size() == 1) {
                    Map<String, Object> deal = datas.get(0);
                    int total_fee = Integer.parseInt((String)orderMap.get("total_fee"));
                    int total_fee_deal = (int) deal.get("total_fee");
                    if (total_fee == total_fee_deal){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
