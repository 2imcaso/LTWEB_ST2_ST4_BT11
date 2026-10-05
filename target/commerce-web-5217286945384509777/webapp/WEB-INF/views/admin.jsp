<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib prefix="c"
	uri="jakarta.tags.core"%><!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Quản lý video</title></head><body>
<div class="dashboard-heading"><div><p class="eyebrow">▤ QUẢN LÝ NỘI DUNG</p><h1>Thư viện video</h1><p class="muted">Thêm nội dung mới, chỉnh sửa thông tin và quản lý trạng thái hiển thị.</p></div><a class="button secondary" href="${pageContext.request.contextPath}/admin">← Tổng quan</a></div>
<section class="admin-grid">
	<div class="panel" id="video-form">
		<h1>${empty editing?'Thêm video':'Cập nhật video'}</h1>
		<c:if test="${not empty error}">
			<p class="error"><c:out value="${error}"/></p>
		</c:if>
		<form method="post" action="${pageContext.request.contextPath}/admin/videos">
			<input type="hidden" name="action" value="save"><label>Mã
				video<input name="id" value="<c:out value='${editing.videoId}'/>"
				${not empty editing?'readonly':''} required>
			</label><label>Tiêu đề<input name="title" value="<c:out value='${editing.title}'/>"
				required></label><label>Poster URL<input name="poster"
				value="<c:out value='${editing.poster}'/>" required></label><label>Lượt xem<input
				type="number" min="0" name="views"
				value="${empty editing?0:editing.views}" required></label><label>Danh
				mục<select name="categoryId" required><c:forEach var="c"
						items="${categories}">
						<option value="${c.categoryId}"
							${editing.category.categoryId==c.categoryId?'selected':''}><c:out value="${c.categoryName}"/></option>
					</c:forEach></select>
			</label><label>Mô tả<textarea name="description" rows="4"><c:out value="${editing.description}"/></textarea></label><label
				class="check"><input type="checkbox" name="active"
				${empty editing||editing.active?'checked':''}> Đang hoạt
				động</label>
			<button>Lưu video</button>
			<c:if test="${not empty editing}">
				<a class="button secondary"
					href="${pageContext.request.contextPath}/admin/videos">Hủy</a>
			</c:if>
		</form>
	</div>
	<div class="panel wide">
		<div class="section-title">
			<h1>Quản lý video</h1>
			<span>6 video / trang</span>
		</div>
		<div class="table-wrap">
			<table>
				<thead>
					<tr>
						<th>Poster</th>
						<th>Mã</th>
						<th>Tiêu đề</th>
						<th>Danh mục</th>
						<th>View</th>
						<th>Trạng thái</th>
						<th>Thao tác</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="v" items="${videos}">
						<tr>
							<td><img class="thumb" src="<c:out value='${v.poster}'/>" alt=""></td>
							<td><c:out value="${v.videoId}"/></td>
							<td><c:out value="${v.title}"/></td>
							<td><c:out value="${v.category.categoryName}"/></td>
							<td>${v.views}</td>
							<td><span class="status-pill ${v.active?'published':''}">${v.active?'Hiển thị':'Ẩn'}</span></td>
							<td><c:url var="editUrl" value="/admin/videos"><c:param name="edit" value="${v.videoId}"/></c:url><a href="${editUrl}#video-form">Sửa</a>
							<form class="inline" method="post" action="${pageContext.request.contextPath}/admin/videos"
									onsubmit="return confirm('Xóa video này?')">
									<input type="hidden" name="action" value="delete"><input
										type="hidden" name="id" value="<c:out value='${v.videoId}'/>">
									<button class="link danger">Xóa</button>
								</form></td>
						</tr>
					</c:forEach>
				<c:if test="${empty videos}"><tr><td colspan="7" class="muted">Chưa có video ở trang này.</td></tr></c:if></tbody>
			</table>
		</div>
		<nav class="pagination">
			<c:forEach begin="1" end="${totalPages}" var="i">
				<a class="${i==page?'active':''}" href="${pageContext.request.contextPath}/admin/videos?page=${i}">${i}</a>
			</c:forEach>
		</nav>
	</div>
</section>
</body></html>

