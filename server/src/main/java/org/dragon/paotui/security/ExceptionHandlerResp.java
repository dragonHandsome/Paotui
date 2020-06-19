package org.dragon.paotui.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.dragon.paotui.enumeration.ErrorResp;
import org.dragon.paotui.payload.ViewData;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Setter
@Getter
@ToString
@Component
public class ExceptionHandlerResp {

    public void send(Integer errCode, String msg, HttpServletResponse response) throws IOException{
            ObjectMapper objectMapper = new ObjectMapper();
            String errMsg = objectMapper.writeValueAsString(
                    ViewData.error(errCode, msg));
            response.setContentType("application/json;charset=utf-8;");
            response.getWriter()
                    .println(errMsg);
            response.getWriter()
                    .flush();
    }
    public void send(ErrorResp errorResp, HttpServletResponse response) throws IOException{
        send(errorResp.getCode(), errorResp.getErrMsg(), response);
    }
}
