package com.example.news;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/news")
public class NewsWebController {
	final NewsDAO dao;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	static final int LISTCOUNT = 5;

	// 프로퍼티 파일로부터 저장 경로 참조
	@Value("${news.imgdir}")
	String fdir;

	@Autowired
	public NewsWebController(NewsDAO dao) {
		this.dao = dao;
	}

	// 뉴스 추가
	@PostMapping("/add")
	public String addNews(@ModelAttribute News news, Model m, @RequestParam("file") MultipartFile file) {
		try {
			// 저장 파일 객체 생성
			File dest = new File(fdir + "/" + file.getOriginalFilename());

			// 파일 저장
			file.transferTo(dest);

			// News 객체에 파일 이름 저장
			news.setImg(dest.getName());
			dao.addNews(news);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("뉴스 추가 과정에서 문제 발생!!");
			m.addAttribute("error", "뉴스가 정상적으로 등록되지 않았습니다!!");
		}
		return "redirect:/news/list";
	}

	// 뉴스 삭제
	@GetMapping("/delete/{aid}")
	public String deleteNews(@PathVariable int aid, Model m) {
		try {
			dao.delNews(aid);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("뉴스 삭제 과정에서 문제 발생!!");
			m.addAttribute("error", "뉴스가 정상적으로 삭제되지 않았습니다!!");
		}
		return "redirect:/news/list";
	}

	// 뉴스 목록
	@GetMapping("/list")
	public String listNews(Model m) {
		List<News> list;
		try {
			list = dao.getAll();
			m.addAttribute("newslist", list);
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("뉴스 목록 생성 과정에서 문제 발생!!");
			m.addAttribute("error", "뉴스 목록이 정상적으로 처리되지 않았습니다!!");
		}
		return "news/newsList";
	}
	
/*	
	// 뉴스 목록
	@GetMapping("/list")
	public String listNews(Model m, HttpServletRequest request) {
		List<News> list;
		int pagenum=1;
		int limit=LISTCOUNT;
		
		if(request.getParameter("pagenum")!=null)
			pagenum=Integer.parseInt(request.getParameter("pagenum"));
		
		String items=request.getParameter("items");
		String text = request.getParameter("text");
		
		int total_record = dao.getlistcount(items, text);

		int total_page;
		
		if(total_record % limit==0) {
			total_page=total_record/limit;
			Math.floor(total_page);
		}
		else {
			total_page = total_record/limit;
			Math.floor(total_page);
			total_page = total_page + 1;
		}
		
		try {
			list = dao.getAll(pagenum, limit, items, text);
			m.addAttribute("newslist", list);
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("뉴스 목록 생성 과정에서 문제 발생!!");
			m.addAttribute("error", "뉴스 목록이 정상적으로 처리되지 않았습니다!!");
		}
		return "news/newsList";
	}
	*/
	// 뉴스 상세
	@GetMapping("/{aid}")
	public String getNews(@PathVariable int aid, Model m) {
		try {
			News n = dao.getNews(aid);
			m.addAttribute("news", n);
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("뉴스를 가져오는 과정에서 문제 발생!!");
			m.addAttribute("error", "뉴스를 정상적으로 가져오지 못했습니다!!");
		}
		return "news/newsView";
	}

	
}