package com.example.news;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import mvc.database.dbconnection;
@Component
public class NewsDAO {

	// 레코드 개수
	public int getlistcount(String items, String text) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		int x = 0;
		String sql;

		if (items == null && text == null)
			sql = "select count(*) from news";
		else
			sql = "SELECT count(*) FROM news where " + items + " like '%" + text + "%'";

		try {
			conn = dbconnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next())
				x = rs.getInt(1);

		} catch (Exception ex) {
			System.out.println("getlistcount() 에러 : " + ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}
		}
		return x;

	}
	
	// 뉴스 추가
	public void addNews(News n) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			String sql = "insert into news(title, img, date, content) value(?,?,CURRENT_TIMESTAMP(),?)";
			conn = dbconnection.getConnection();
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, n.getTitle());
			pstmt.setString(2, n.getImg());
			pstmt.setString(3, n.getContent());
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}
		}
	}
	public List<News> getAll() throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<News> newsList = new ArrayList<>();

		String sql = "select aid, title, date as cdate from news";
		
		try {
			conn = dbconnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				News n = new News();
				n.setAid(rs.getInt("aid"));
				n.setTitle(rs.getString("title"));
				n.setDate(rs.getString("cdate"));

				newsList.add(n);
			}
			return newsList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}
		}
		return null;
	}
	
	// 뉴스 기사 목록 전체 가져오기
	public List<News> getAll(int page, int limit, String items, String text) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<News> newsList = new ArrayList<>();


		int total_record = getlistcount(items, text);
		int start = (page - 1) * limit;
		int index = start + 1;

		String sql;
		
		if (items == null && text == null)
			sql = "select aid, title, date as cdate from news";
		else
			sql = "SELECT  aid, title, date as cdate FROM board where " + items + " like '%" + text + "%' ORDER BY aid DESC ";
		
		try {
			conn = dbconnection.getConnection();
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = pstmt.executeQuery();
/*
			while (rs.next()) {
				News n = new News();
				n.setAid(rs.getInt("aid"));
				n.setTitle(rs.getString("title"));
				n.setDate(rs.getString("cdate"));

				newsList.add(n);
			}*/
			while (rs.absolute(index)) {
				News n = new News();
				n.setAid(rs.getInt("aid"));
				n.setTitle(rs.getString("title"));
				n.setDate(rs.getString("cdate"));

				newsList.add(n);
				
				if (index < (start + limit) && index <= total_record)
					index++;
				else
					break;
				
			}
			return newsList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}
		}
		return null;
	}

	// 선택된 뉴스 보기
	public News getNews(int aid) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		News n = new News();
		String sql = "select aid, title, img,  date as cdate, content from news where aid=?";

		try {
			conn = dbconnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, aid);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				n.setAid(rs.getInt("aid"));
				n.setTitle(rs.getString("title"));
				n.setImg(rs.getString("img"));
				n.setDate(rs.getString("cdate"));
				n.setContent(rs.getString("content"));
				pstmt.executeQuery();
			}
			return n;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}
		}
		return null;
	}

	public void delNews(int aid) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "delete from news where aid=?";

		try {
			conn = dbconnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, aid);
			pstmt.executeUpdate();

			if (pstmt.executeUpdate() == 0) {
				throw new SQLException("DB에러");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}
		}
	}

}
