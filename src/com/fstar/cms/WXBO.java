package com.fstar.cms;

import com.fstar.sys.DB;
import com.fstar.utility.Machine;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/WXBO")
public class WXBO  extends HttpServlet {

    // 统一下单：
    public Map<String, Object> get_code_url(Map<String, Object> map)
            throws Exception {
        Map<String, Object> returnmap = new HashMap<String, Object>();

        try {
            WXConfig config = new WXConfig();
            WXPay wxpay = new WXPay(config);

            Map<String, String> data = new HashMap<String, String>();
            data.put("body", (String) map.get("body"));
            data.put("out_trade_no", UUID.randomUUID().toString().replace("-",""));
            data.put("device_info", (String) map.get("device_info"));
            data.put("fee_type", "CNY");
            data.put("total_fee", (String) map.get("total_fee"));
            data.put("trade_type", "NATIVE");  // 此处指定为扫码支付
            data.put("product_id", (String) map.get("product_id"));
            data.put("attach", (String) map.get("attach"));

            //参数服务器终端IP
            data.put("spbill_create_ip", "123.12.12.123");
            //通知地址
            data.put("notify_url", "http://www.example.com/wxpay/notify");

            returnmap.put("out_trade_no",data.get("out_trade_no"));

            Map<String, String> resp = wxpay.unifiedOrder(data);
            System.out.println(resp);
            if (resp.get("code_url") != null){
                returnmap.put("code_url",resp.get("code_url"));
                //记录临时订单
                Map<String, Object> recordData = new HashMap<String, Object>();
                recordData.putAll(data);
                recordData.putAll(resp);
                DB.insert("fs_deal", recordData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        returnmap.put("code_url","http://www.sohu.com");
        return returnmap;
    }

    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            Enumeration<String> e = req.getParameterNames();
            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                data.put(name, req.getParameter(name));
            }

            //更新交易
            Map<String, Object> recordData = new HashMap<String, Object>();
            recordData.putAll(data);
            DB.update("fs_deal", recordData);

            //记录VIP用户
            data.put("validity", Machine.getDateAddByMonth(Integer.parseInt((String) data.get("attach"))));
            data.put("remark", "VIP付费月数:"+data.get("attach"));
            DB.update("fs_terminal", data);

            //回传信息
            Map<String, String> output = new HashMap<String, String>();
            output.put("return_code","SUCCESS");
            output.put("return_msg","OK");

            res.setContentType("application/xml;charset=utf-8");
            PrintWriter out = res.getWriter();
            out.print(WXPayUtil.mapToXml(output));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    private boolean check() throws Exception {
        String notifyData = "...."; // 支付结果通知的xml格式数据

        WXConfig config = new WXConfig();
        WXPay wxpay = new WXPay(config);

        Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyData);  // 转换成map

        if (wxpay.isPayResultNotifySignatureValid(notifyMap)) {
            // 签名正确
            // 进行处理。
            // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
            return true;
        }
        else {
            // 签名错误，如果数据里没有sign字段，也认为是签名错误
            return false;
        }
    }


}
