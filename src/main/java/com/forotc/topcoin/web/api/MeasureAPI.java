//package com.forotc.topcoin.web.api;
//
//import com.forotc.topcoin.web.api.po.RegisterUserKeys;
//import com.forotc.topcoin.web.svc.WsSvc;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.concurrent.atomic.AtomicLong;
//
//@RestController
//
////@ServerEndpoint("/wss/{uid}")
//@Slf4j
//public class MeasureAPI {
//
//	@Autowired
//	WsSvc wsSvc ;
//
//	private static final Logger logger = LoggerFactory.getLogger(MeasureAPI.class);
//
//	private static final AtomicLong onlineCount = new AtomicLong(0);
//
//	@RequestMapping(value = "/getuidsessions", method = RequestMethod.GET)
//	@ResponseBody
//	public String getUidSessions() {
//		System.out.println("getuidsessions" + wsSvc);
//	    return wsSvc.getUidSessions();
//	}
//
//	@RequestMapping(value = "/getuidkeyflags", method = RequestMethod.GET)
//	@ResponseBody
//	public String getuidKeyFlags() {
//		System.out.println("getUidKeyFlags" + wsSvc);
//	    return wsSvc.getUidKeyFlags();
//	}
//
//	@RequestMapping(value = "/fireall", method = RequestMethod.POST, consumes = {"application/json","application/xml"}, produces = { "application/json", "application/xml" })
//	@ResponseStatus(HttpStatus.OK)
//	@ResponseBody
//	public String setFireall(@RequestBody RegisterUserKeys ruk, HttpServletRequest request, HttpServletResponse response) {
//		log.info("setFireall:" + this.toString() + ":" + ruk.getUid());
//		if(ruk.getUid()!=null && ruk.getUid().equals("8888")) {
//			wsSvc.setFireall(true);
//			return "WServer changed to fire-all-mode";
//		} else {
//			return "You have no right to fire-all!";
//		}
//
//	}
//
//	@RequestMapping(value = "/unfireall", method = RequestMethod.POST, consumes = {"application/json","application/xml"}, produces = { "application/json", "application/xml" })
//	@ResponseStatus(HttpStatus.OK)
//	@ResponseBody
//	public String setFireallNot(@RequestBody RegisterUserKeys ruk, HttpServletRequest request, HttpServletResponse response) {
//		log.info("setFireall:" + this.toString() + ":" + ruk.getUid());
//		if(ruk.getUid()!=null && ruk.getUid().equals("8888")) {
//			wsSvc.setFireall(false);
//			return "WServer changed to normal mode!";
//		} else {
//			return "You have no right to fire-all!";
//		}
//
//	}
//
//	@RequestMapping(value = "/wsdebug", method = RequestMethod.POST, consumes = {"application/json","application/xml"}, produces = { "application/json", "application/xml" })
//	@ResponseStatus(HttpStatus.OK)
//	@ResponseBody
//	public String setWsdebug(@RequestBody RegisterUserKeys ruk, HttpServletRequest request, HttpServletResponse response) {
//		log.info("setFireall:" + this.toString() + ":" + ruk.getUid());
//		if(ruk.getUid()!=null && ruk.getUid().equals("8888")) {
//			wsSvc.setWsdebug(true);
//			return "WServer changed  to ws debug mode!";
//		} else {
//			return "You have no right!";
//		}
//
//	}
//
//	@RequestMapping(value = "/unwsdebug", method = RequestMethod.POST, consumes = {"application/json","application/xml"}, produces = { "application/json", "application/xml" })
//	@ResponseStatus(HttpStatus.OK)
//	@ResponseBody
//	public String unWsdebug(@RequestBody RegisterUserKeys ruk, HttpServletRequest request, HttpServletResponse response) {
//		log.info("setFireall:" + this.toString() + ":" + ruk.getUid());
//		if(ruk.getUid()!=null && ruk.getUid().equals("8888")) {
//			wsSvc.setWsdebug(false);
//			return "WServer changed to non ws debug mode!";
//		} else {
//			return "You have no right!";
//		}
//
//	}
//
//
//}
//
////@OnOpen
////public void onOpen(Session session, @PathParam("uid") String uid) {
////	log.info("onOpen.uid:" + uid + wsSvc);
////	if(!wsSvc.addSession(uid, session)) {
////		try {
////			session.getBasicRemote().sendText("Uid(" + uid +" already opened, please make sure it unique!");
////			session.close();
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////			log.error("onOpen: session.getBasicRemote().sendText(msg) failed for UID:" + uid);
////		}
////	}
////	log.info("New one opened, current connections:" + this.toString() + ": " + onlineCount.incrementAndGet());
//////	wsSvc.sendMsg(uid, ">>>  Welcome to WS Server >>>");
////}
////