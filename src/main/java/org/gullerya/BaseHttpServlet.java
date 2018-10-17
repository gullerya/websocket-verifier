package org.gullerya;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BaseHttpServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setStatus(200);
		resp.getWriter().write("hello world");
		resp.flushBuffer();
	}
}
