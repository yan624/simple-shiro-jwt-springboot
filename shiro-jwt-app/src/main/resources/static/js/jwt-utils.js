function clearAllToken(){
    // clear all information
    localStorage.setItem("Authorization", "")
    localStorage.setItem("refresh_token", "")
    // ...
}

function checkAndReturnToken(){
    let accessToken = localStorage.getItem("Authorization");
    let refreshToken = localStorage.getItem("refresh_token");

    if (accessToken === '' || refreshToken === '' || accessToken == null || refreshToken == null){
        return [false, 0, ''];
    }

    let ap = KJUR.jws.JWS.readSafeJSONString(b64utoutf8(accessToken.split(".")[1]));
    let rp = KJUR.jws.JWS.readSafeJSONString(b64utoutf8(refreshToken.split(".")[1]));
    // jwt 存储的时间默认单位是秒
    let aexp = ap.exp * 1000;
    let rexp = rp.exp * 1000;

    let success;
    let code;
    let jwt;
    let refreshUrl = "http://localhost:8080/refresh?grantType=refresh_token&refreshToken=" + refreshToken;
    // 如果 jwt 已过期且 refreshToken 未过期（过期时长大于 30 分钟）
    if (Date.now() > aexp && Date.now() < rexp){
        // 刷新令牌之后，返回 true
        $.post({
            url: refreshUrl,
            headers: {"Authorization": "Bearer " + accessToken},
            contentType: "application/x-www-form-urlencoded",
            async: false,
            success: function(data) {
                success = true;
                code = 200;
                localStorage.setItem("Authorization", data);
                jwt = localStorage.getItem("Authorization");
            },error: function(XMLHttpRequest, textStatus, errorThrown){
                success = false;
                code = 500;
                jwt = '';
                //查看错误信息
                console.log(XMLHttpRequest.status);
                console.log(XMLHttpRequest.readyState);
                console.log(textStatus);
            }
        });
    }else if(Date.now() > rexp) {
        clearAllToken()
        // 如果 refreshToken 都过期了，那么让用户重新登录。
        success = false;
        code = 0;
        jwt = '';
    }else{
        success = true;
        code = 2;
        jwt = localStorage.getItem("Authorization");
    }
    return [success, code, jwt];
}
