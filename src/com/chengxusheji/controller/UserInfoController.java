package com.chengxusheji.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.chengxusheji.utils.ExportExcelUtil;
import com.chengxusheji.utils.UserException;
import com.chengxusheji.service.UserInfoService;
import com.chengxusheji.po.UserInfo;

//UserInfo管理控制层
@Controller
@RequestMapping("/UserInfo")
public class UserInfoController extends BaseController {

    /*业务层对象*/
    @Resource UserInfoService userInfoService;

	@InitBinder("userInfo")
	public void initBinderUserInfo(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("userInfo.");
	}
	/*跳转到添加UserInfo视图*/
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model,HttpServletRequest request) throws Exception {
		model.addAttribute(new UserInfo());
		return "UserInfo_add";
	}

	/*客户端ajax方式提交添加用户信息信息*/
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public void add(@Validated UserInfo userInfo, BindingResult br,
			Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String message = "";
		boolean success = false;
		if (br.hasErrors()) {
			message = "输入信息不符合要求！";
			writeJsonResponse(response, success, message);
			return ;
		}
		if(userInfoService.getUserInfo(userInfo.getUserInfoname()) != null) {
			message = "用户名已经存在！";
			writeJsonResponse(response, success, message);
			return ;
		}
		try {
			userInfo.setUserPhoto(this.handlePhotoUpload(request, "userPhotoFile"));
		} catch(UserException ex) {
			message = "图片格式不正确！";
			writeJsonResponse(response, success, message);
			return ;
		}
        userInfoService.addUserInfo(userInfo);
        message = "用户信息添加成功!";
        success = true;
        writeJsonResponse(response, success, message);
	}
	/*ajax方式按照查询条件分页查询用户信息信息*/
	@RequestMapping(value = { "/list" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void list(String userInfoname,String name,String birthday,String telephone,Integer page,Integer rows, Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		if (page==null || page == 0) page = 1;
		if (userInfoname == null) userInfoname = "";
		if (name == null) name = "";
		if (birthday == null) birthday = "";
		if (telephone == null) telephone = "";
		if(rows != 0)userInfoService.setRows(rows);
		List<UserInfo> userInfoList = userInfoService.queryUserInfo(userInfoname, name, birthday, telephone, page);
	    /*计算总的页数和总的记录数*/
	    userInfoService.queryTotalPageAndRecordNumber(userInfoname, name, birthday, telephone);
	    /*获取到总的页码数目*/
	    int totalPage = userInfoService.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = userInfoService.getRecordNumber();
        response.setContentType("text/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		//将要被返回到客户端的对象
		JSONObject jsonObj=new JSONObject();
		jsonObj.accumulate("total", recordNumber);
		JSONArray jsonArray = new JSONArray();
		for(UserInfo userInfo:userInfoList) {
			JSONObject jsonUserInfo = userInfo.getJsonObject();
			jsonArray.put(jsonUserInfo);
		}
		jsonObj.accumulate("rows", jsonArray);
		out.println(jsonObj.toString());
		out.flush();
		out.close();
	}

	/*ajax方式按照查询条件分页查询用户信息信息*/
	@RequestMapping(value = { "/listAll" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void listAll(HttpServletResponse response) throws Exception {
		List<UserInfo> userInfoList = userInfoService.queryAllUserInfo();
        response.setContentType("text/json;charset=UTF-8"); 
		PrintWriter out = response.getWriter();
		JSONArray jsonArray = new JSONArray();
		for(UserInfo userInfo:userInfoList) {
			JSONObject jsonUserInfo = new JSONObject();
			jsonUserInfo.accumulate("userInfoname", userInfo.getUserInfoname());
			jsonUserInfo.accumulate("name", userInfo.getName());
			jsonArray.put(jsonUserInfo);
		}
		out.println(jsonArray.toString());
		out.flush();
		out.close();
	}

	/*前台按照查询条件分页查询用户信息信息*/
	@RequestMapping(value = { "/frontlist" }, method = {RequestMethod.GET,RequestMethod.POST})
	public String frontlist(String userInfoname,String name,String birthday,String telephone,Integer currentPage, Model model, HttpServletRequest request) throws Exception  {
		if (currentPage==null || currentPage == 0) currentPage = 1;
		if (userInfoname == null) userInfoname = "";
		if (name == null) name = "";
		if (birthday == null) birthday = "";
		if (telephone == null) telephone = "";
		List<UserInfo> userInfoList = userInfoService.queryUserInfo(userInfoname, name, birthday, telephone, currentPage);
	    /*计算总的页数和总的记录数*/
	    userInfoService.queryTotalPageAndRecordNumber(userInfoname, name, birthday, telephone);
	    /*获取到总的页码数目*/
	    int totalPage = userInfoService.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = userInfoService.getRecordNumber();
	    request.setAttribute("userInfoList",  userInfoList);
	    request.setAttribute("totalPage", totalPage);
	    request.setAttribute("recordNumber", recordNumber);
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("userInfoname", userInfoname);
	    request.setAttribute("name", name);
	    request.setAttribute("birthday", birthday);
	    request.setAttribute("telephone", telephone);
		return "UserInfo/userInfo_frontquery_result"; 
	}

     /*前台查询UserInfo信息*/
	@RequestMapping(value="/{userInfoname}/frontshow",method=RequestMethod.GET)
	public String frontshow(@PathVariable String userInfoname,Model model,HttpServletRequest request) throws Exception {
		/*根据主键userInfoname获取UserInfo对象*/
        UserInfo userInfo = userInfoService.getUserInfo(userInfoname);

        request.setAttribute("userInfo",  userInfo);
        return "UserInfo/userInfo_frontshow";
	}

	/*ajax方式显示用户信息修改jsp视图页*/
	@RequestMapping(value="/{userInfoname}/update",method=RequestMethod.GET)
	public void update(@PathVariable String userInfoname,Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
        /*根据主键userInfoname获取UserInfo对象*/
        UserInfo userInfo = userInfoService.getUserInfo(userInfoname);

        response.setContentType("text/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
		//将要被返回到客户端的对象 
		JSONObject jsonUserInfo = userInfo.getJsonObject();
		out.println(jsonUserInfo.toString());
		out.flush();
		out.close();
	}

	/*ajax方式更新用户信息信息*/
	@RequestMapping(value = "/{userInfoname}/update", method = RequestMethod.POST)
	public void update(@Validated UserInfo userInfo, BindingResult br,
			Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String message = "";
    	boolean success = false;
		if (br.hasErrors()) { 
			message = "输入的信息有错误！";
			writeJsonResponse(response, success, message);
			return;
		}
		String userPhotoFileName = this.handlePhotoUpload(request, "userPhotoFile");
		if(!userPhotoFileName.equals("upload/NoImage.jpg"))userInfo.setUserPhoto(userPhotoFileName); 


		try {
			userInfoService.updateUserInfo(userInfo);
			message = "用户信息更新成功!";
			success = true;
			writeJsonResponse(response, success, message);
		} catch (Exception e) {
			e.printStackTrace();
			message = "用户信息更新失败!";
			writeJsonResponse(response, success, message); 
		}
	}
    /*删除用户信息信息*/
	@RequestMapping(value="/{userInfoname}/delete",method=RequestMethod.GET)
	public String delete(@PathVariable String userInfoname,HttpServletRequest request) throws UnsupportedEncodingException {
		  try {
			  userInfoService.deleteUserInfo(userInfoname);
	            request.setAttribute("message", "用户信息删除成功!");
	            return "message";
	        } catch (Exception e) { 
	            e.printStackTrace();
	            request.setAttribute("error", "用户信息删除失败!");
				return "error";

	        }

	}

	/*ajax方式删除多条用户信息记录*/
	@RequestMapping(value="/deletes",method=RequestMethod.POST)
	public void delete(String userInfonames,HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException {
		String message = "";
    	boolean success = false;
        try { 
        	int count = userInfoService.deleteUserInfos(userInfonames);
        	success = true;
        	message = count + "条记录删除成功";
        	writeJsonResponse(response, success, message);
        } catch (Exception e) { 
            //e.printStackTrace();
            message = "有记录存在外键约束,删除失败";
            writeJsonResponse(response, success, message);
        }
	}

	/*按照查询条件导出用户信息信息到Excel*/
	@RequestMapping(value = { "/OutToExcel" }, method = {RequestMethod.GET,RequestMethod.POST})
	public void OutToExcel(String userInfoname,String name,String birthday,String telephone, Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
        if(userInfoname == null) userInfoname = "";
        if(name == null) name = "";
        if(birthday == null) birthday = "";
        if(telephone == null) telephone = "";
        List<UserInfo> userInfoList = userInfoService.queryUserInfo(userInfoname,name,birthday,telephone);
        ExportExcelUtil ex = new ExportExcelUtil();
        String _title = "UserInfo信息记录"; 
        String[] headers = { "用户名","登录密码","姓名","性别","出生日期","联系电话","邮箱地址","个人照片"};
        List<String[]> dataset = new ArrayList<String[]>(); 
        for(int i=0;i<userInfoList.size();i++) {
        	UserInfo userInfo = userInfoList.get(i); 
        	dataset.add(new String[]{userInfo.getUserInfoname(),userInfo.getPassword(),userInfo.getName(),userInfo.getSex(),userInfo.getBirthday(),userInfo.getTelephone(),userInfo.getEmail(),userInfo.getUserPhoto()});
        }
        /*
        OutputStream out = null;
		try {
			out = new FileOutputStream("C://output.xls");
			ex.exportExcel(title,headers, dataset, out);
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		OutputStream out = null;//创建一个输出流对象 
		try { 
			out = response.getOutputStream();//
			response.setHeader("Content-disposition","attachment; filename="+"UserInfo.xls");//filename是下载的xls的名，建议最好用英文 
			response.setContentType("application/msexcel;charset=UTF-8");//设置类型 
			response.setHeader("Pragma","No-cache");//设置头 
			response.setHeader("Cache-Control","no-cache");//设置头 
			response.setDateHeader("Expires", 0);//设置日期头  
			String rootPath = request.getSession().getServletContext().getRealPath("/");
			ex.exportExcel(rootPath,_title,headers, dataset, out);
			out.flush();
		} catch (IOException e) { 
			e.printStackTrace(); 
		}finally{
			try{
				if(out!=null){ 
					out.close(); 
				}
			}catch(IOException e){ 
				e.printStackTrace(); 
			} 
		}
    }
}