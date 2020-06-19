package org.dragon.paotui.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WechatUserDetail implements Serializable {
    @Id
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date birthday;

    private String school;

    private String phone;

    private String backgroundImage;

    private String avatar;

    private Double score;

    private String motto;

    private Long userId;

    public String getAvatar(){
        if(this.avatar != null && !this.avatar.endsWith("-min.jpg")){
            return this.avatar.replace(".jpg", "-min.jpg");
        }
        return this.avatar;
    }
}