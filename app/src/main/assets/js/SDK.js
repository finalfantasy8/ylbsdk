var smk = {
	attribute: {
		appid: "", // 接入的H5app编号
		appname: "", // 接入的H5app名称
	},
	init: function(obj) {
		if(obj && obj.appid && obj.appname) {
			this.attribute.appid = obj.appid;
			this.attribute.appname = obj.appname;
		} else {
			alert("请输入完整的app信息");
		}
	},
	toPay: function(obj) {
		console.log("toPay Start")
		if(this.attribute.appid && this.attribute.appname) {
			if(obj.orderNo && obj.dateTime && obj.amount && obj.cardnumber && obj.goods && obj.merCode && obj.mersign && obj.mertxtypeid && obj.storeid) {
				var orderInfo = {
					appid: this.attribute.appid,
					appname: this.attribute.appname,
					params: {
						"orderNo": obj.orderNo,
						"dateTime": obj.dateTime,
						"amount": "" + obj.amount,
						"cardnumber": obj.cardnumber,
						"goods": obj.goods,
						"merCode": obj.merCode,
						"mersign": obj.mersign,
						"mertxtypeid": obj.mertxtypeid,
						"storeid": obj.storeid
					},
					callback: "smk.sendPayResultToWap"
				}
				var srcs = BASE64.encoder(JSON.stringify(orderInfo));
				var url = "ylbsdk://pay/" + srcs;
				location.href = url;
			} else {
				alert("请输入正确的参数，且参数不能为空");
			}
		} else {
			alert("请输入完整的app信息");
		}

	},
	sendPayResultToWap: function(result) {
		$(".payResult").html("payResult" + result)
//		var json = JSON.parse(toStr(BASE64.decoder(result)));
		$(".payResult").html($(".payResult").html() + "<br/> pay:" + toStr(BASE64.decoder(result)))
	},
	toLogin: function(obj) {
		console.log("toLogin Start")
		if(this.attribute.appid && this.attribute.appname) {
			if(obj.phoneNo && obj.userName) {
				var orderInfo = {
					appid: this.attribute.appid,
					appname: this.attribute.appname,
					params: {
						phoneNo: obj.phoneNo,
						userName: obj.userName
					},
					callback: "smk.sendLoginResultToWap"
				}
				var srcs = BASE64.encoder(JSON.stringify(orderInfo));
				var url = "ylbsdk://login/" + srcs;
				location.href = url;
			} else {
				alert("请输入正确的参数，且参数不能为空");
			}
		} else {
			alert("请输入完整的app信息");
		}

	},
	sendLoginResultToWap: function(result) {
		$(".loginResult").html("loginResult" + result)
//		var json = JSON.parse(toStr(BASE64.decoder(result)));
		$(".loginResult").html($(".loginResult").html() + "<br/> login:" + toStr(BASE64.decoder(result)))
	},
	toToast: function(obj) {
		console.log("toToast Start")
		if(this.attribute.appid && this.attribute.appname) {
			if(obj.msg && obj.type) {
				var orderInfo = {
					appid: this.attribute.appid,
					appname: this.attribute.appname,
					params: {
						msg: obj.msg,
						type: obj.type
					},
					callback: "smk.sendToastResultToWap"
				}
				var srcs = BASE64.encoder(JSON.stringify(orderInfo));
				var url = "ylbsdk://toast/" + srcs;
				location.href = url;
			} else {
				alert("请输入正确的参数，且参数不能为空");
			}
		} else {
			alert("请输入完整的app信息");
		}

	},
	sendToastResultToWap: function(result) {
		$(".toastResult").html("toastResult" + result)
//		var json = JSON.parse(toStr(BASE64.decoder(result)));
		$(".toastResult").html($(".toastResult").html() + "<br/> toast:" + toStr(BASE64.decoder(result)))
	},
	toDialog: function(obj) {
		console.log("toDialog Start")
		if(this.attribute.appid && this.attribute.appname) {
			if(obj.title && obj.msg&&obj.type) {
				var orderInfo = {
					appid: this.attribute.appid,
					appname: this.attribute.appname,
					params: {
						title:obj.title,//"这是标题",
						msg:obj.msg, //"这是问题",
						confirm:obj.confirm!=undefined?obj.confirm:"确定",//"是 按钮",
						cancel:obj.cancel!=undefined?obj.cancel:"取消",//"否 按钮",
						type:obj.type, //"类型",1，一个按钮 只有confirm， 2， 两个按钮
					},
					callback: "smk.sendDialogResultToWap"
				}
				var srcs = BASE64.encoder(JSON.stringify(orderInfo));
				var url = "ylbsdk://dialog/" + srcs;
				location.href = url;
			} else {
				alert("请输入正确的参数，且参数不能为空");
			}
		} else {
			alert("请输入完整的app信息");
		}

	},
	sendDialogResultToWap: function(result) {
		$(".dialogResult").html("dialogResult" + result)
//		var json = JSON.parse(toStr(BASE64.decoder(result)));
		$(".dialogResult").html($(".dialogResult").html() + "<br/> dialog:" + toStr(BASE64.decoder(result)))
	},
	toFunction:function(obj,fn) {
		console.log("toFunction Start")
		if(this.attribute.appid && this.attribute.appname) {
			if(obj.msg && obj.type) {
				var orderInfo = {
					appid: this.attribute.appid,
					appname: this.attribute.appname,
					params: {
						msg: obj.msg,
						type: obj.type
					},
					callback: "(" + (fn!=undefined?fn:function(str) {alert(str)}) + ")"

				}
				var srcs = BASE64.encoder(JSON.stringify(orderInfo));
				var url = "ylbsdk://toast/" + srcs;
				location.href = url;
			} else {
				alert("请输入正确的参数，且参数不能为空");
			}
		} else {
			alert("请输入完整的app信息");
		}

	},
    toFinish: function(obj) {
        console.log("toFinish Start")
        location.href = "ylbsdk://finish/";
    },
    toFunction1: function(result) {
        $(".functionResult").html("functionResult" + result)
//        var json = JSON.parse(toStr(BASE64.decoder(result)));
        $(".functionResult").html($(".functionResult").html() + "<br/> function:" + toStr(BASE64.decoder(result)))
    },
    toGetparam:function(fn) {
        console.log("toGetparam Start")
        if(this.attribute.appid && this.attribute.appname) {
            var orderInfo = {
                appid: this.attribute.appid,
                appname: this.attribute.appname,
                callback: "(" + (fn!=undefined?fn:function(str) {alert(str)}) + ")"

            }
            var srcs = BASE64.encoder(JSON.stringify(orderInfo));
            var url = "ylbsdk://getparam/" + srcs;
            location.href = url;
        } else {
            alert("请输入完整的app信息");
        }

    },
    toSetparam: function(str) {
        console.log("toFinish Start")
        if(this.attribute.appid && this.attribute.appname) {
            var orderInfo = {
                appid: this.attribute.appid,
                appname: this.attribute.appname,
                params: str,
            }
            var srcs = BASE64.encoder(JSON.stringify(orderInfo));
            var url = "ylbsdk://setparam/" + srcs;
            location.href = url;
        } else {
            alert("请输入完整的app信息");
        }
    },
}