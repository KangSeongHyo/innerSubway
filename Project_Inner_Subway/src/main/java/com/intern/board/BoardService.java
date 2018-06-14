package com.intern.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.intern.dao.BoardDAO;
import com.intern.station.StationVO;
import com.mysql.cj.api.Session;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.intern.board.BoardVO;
import com.intern.check.Check;
import com.intern.board.BoardService;
import com.intern.comment.CommentVO;
import com.intern.comment.CommentService;
import com.intern.station.StationService;
import com.intern.station.StationVO;

/*@RunWith(SpringJUnit4ClassRunner.class) //d요거로 테스트하겠다
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/**.xml")// xml 위치설정 스프링로드*/
@Component
public class BoardService implements Board {

	@Autowired
	BoardDAO dao;

	public BoardVO getBoardOne(BoardVO requstBoard, String id) {

		if (!id.equals("load")) {
			if (requstBoard.getWriter().equals(id)) {
				return dao.getBoardOne(requstBoard);

			} else {

				return null;
			}
		} else {

			return dao.getBoardOne(requstBoard);
		}

	}

	@Override
	public List<BoardVO> getBoardList(StationVO requestStation, int page) {

		int boardCountPage = 4;
		int startBoard = (page - 1) * boardCountPage;

		return dao.getBoardList(requestStation, startBoard);
	}

	@Override
	public int getEntryCount(StationVO requestStation) {
		return dao.getEntryCount(requestStation);
	}

	@Override
	public int romoveBoard(BoardVO requestBoard, String id) {

		int check;

		if (requestBoard.getWriter().equals(id)) {
			check = dao.removeBoard(requestBoard);

		} else {
			check = Check.NOAUTH;

		}

		return check;
	}

	@Override
	public int modifyBoard(BoardVO requestBoard, MultipartFile file) {
		//////////////file 업로드
		int check = Check.FAIL;

		try {

			if (file != null) {
				String realName = file.getOriginalFilename();

				String ext = realName.substring(realName.lastIndexOf("."));// 확장자 추출

				String uuid = UUID.randomUUID().toString().replaceAll("-", "");//-제거  

				String uniqueName = uuid + ext; //유니크한 이름 생성 

				//String uploadPath = "/home1/irteam/apps/apache-tomcat-8.5.23/webapps/storage/" + uniqueName;
				String uploadPath = "C:/new/" + uniqueName;

				String imgPath = "/storage/" + uniqueName;

				File newfile = new File(uploadPath);

				file.transferTo(newfile);

				requestBoard.setImgPath(imgPath);
			}
			check = dao.modifyBoard(requestBoard);

		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}

		return check;
	}

	@Override
	public int boardRegister(BoardVO requestBoard, MultipartFile file) {
		//////////////file 업로드
		try {

			String realName = file.getOriginalFilename();

			String ext = realName.substring(realName.lastIndexOf("."));// 확장자 추출

			String uuid = UUID.randomUUID().toString().replaceAll("-", "");//-제거  

			String uniqueName = uuid + ext; //유니크한 이름 생성 

			//String uploadPath = "/home1/irteam/apps/apache-tomcat-8.5.23/webapps/storage/" + uniqueName;
			String uploadPath = "C:/new/" + uniqueName;

			String imgPath = "/storage/" + uniqueName;

			File newfile = new File(uploadPath);

			file.transferTo(newfile);

			requestBoard.setImgPath(imgPath);

		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}

		return dao.boardRegister(requestBoard);
	}

	/*@Override
	
	
	@Override
	public void updateViewcount(Map map) {
		// TODO Auto-generated method stub
		dao.updateViewcount(map);
	}
	
	
	@Override
	public void updateBoard(BoardVO vo) {
		// TODO Auto-generated method stub
		dao.updateBoard(vo);
	}
	
	@Override
	public List<BoardVO> getSearchBoard(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return dao.getSearchBoard(map);
	}*/

	@Override
	public List<BoardVO> removeHtml(List<BoardVO> boardList) {

		for (BoardVO vo : boardList) {
			vo.setContent(remove(vo.getContent()));
		}

		return boardList;
	}

	@Override
	public Map<String, Integer> getPage(int page, StationVO requestStation) {
		/////////페이징 처리
		int totalBoard = dao.getEntryCount(requestStation);// 전체 게시물개수

		System.out.println(totalBoard);
		int boardCountPage = 4;//한 페이지에 보여줄 게시물개수

		int totalPage = (totalBoard % boardCountPage == 0) ? (totalBoard / boardCountPage) : (totalBoard / boardCountPage + 1); //전체페이지수 

		System.out.println(totalPage);

		int pageCountList = 3; //한번에 보여줄 페이지목록의 수

		// 현재 페이지 기준으로 시작페이지 끝페이지 
		int startPage = ((page - 1) / pageCountList) * pageCountList + 1;// -1 하는것은 나누기때문에  +1 1페이지 기준 // * 묶음으로 나오므로 startPage를 구해야하기때문에

		int endPage = startPage + pageCountList - 1;

		// 마지막 페이지 처리 
		if (endPage > totalPage) {
			endPage = totalPage;
		}

		Map<String, Integer> pageMap = new HashMap<String, Integer>();
		pageMap.put("startPage", startPage);
		pageMap.put("endPage", endPage);
		pageMap.put("totalPage", totalPage);
		/////페이징처리 게시물
		return pageMap;
	}

	////////////html코드삭제
	private String remove(String content) {
		Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);
		Pattern STYLE = Pattern.compile("<style[^>]*>.*</style>", Pattern.DOTALL);
		Pattern TAGS = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
		Pattern ENTITY_REFS = Pattern.compile("&[^;]+;");
		Pattern WHITESPACE = Pattern.compile("\\s\\s+");
		Matcher m;
		m = SCRIPTS.matcher(content);
		content = m.replaceAll("");
		m = STYLE.matcher(content);
		content = m.replaceAll("");
		m = TAGS.matcher(content);
		content = m.replaceAll("");
		m = ENTITY_REFS.matcher(content);
		content = m.replaceAll("");
		m = WHITESPACE.matcher(content);
		content = m.replaceAll(" ");
		return content;
	}

}