<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><title>Trang chủ</title></head><body>
<c:set var="featured" value="${null}"/>
<c:set var="totalVideos" value="0"/>
<c:forEach var="entry" items="${groups}"><c:set var="totalVideos" value="${totalVideos + counts[entry.key.categoryId]}"/><c:if test="${empty featured and not empty entry.value}"><c:set var="featured" value="${entry.value[0]}"/></c:if></c:forEach>
<section class="hero">
<c:if test="${not empty featured}"><img class="hero-image" src="<c:out value='${featured.poster}'/>" alt=""></c:if>
<div class="hero-content"><div class="badges"><span class="badge red">● VIDEO NỔI BẬT</span><c:if test="${not empty featured}"><span class="badge"><c:out value="${featured.category.categoryName}"/></span></c:if></div>
<h1><c:choose><c:when test="${not empty featured}"><c:out value="${featured.title}"/></c:when><c:otherwise>Thế giới video dành cho bạn</c:otherwise></c:choose></h1>
<p class="hero-description"><c:choose><c:when test="${not empty featured.description}"><c:out value="${featured.description}"/></c:when><c:otherwise>Khám phá những câu chuyện mới, tri thức và cảm hứng theo từng danh mục.</c:otherwise></c:choose></p>
<div class="hero-actions"><c:if test="${not empty featured}"><c:url var="featuredUrl" value="/video"><c:param name="id" value="${featured.videoId}"/></c:url><a class="button" href="${featuredUrl}">▶ Xem chi tiết</a><span>${featured.views} lượt xem</span></c:if><a class="button secondary" href="#categories">Khám phá danh mục</a></div></div>
</section>
<section id="categories" class="category-summary"><div class="section-title"><h2>Thống kê video theo danh mục</h2><span>Tổng cộng: <strong>${totalVideos} video</strong></span></div><nav class="category-pills" aria-label="Danh mục video"><a class="badge selected" href="#video-list">Tất cả <b>${totalVideos}</b></a><c:forEach var="entry" items="${groups}"><a class="badge" href="#category-${entry.key.categoryId}"><c:out value="${entry.key.categoryName}"/> <b>${counts[entry.key.categoryId]}</b></a></c:forEach></nav></section>
<div id="video-list"><c:forEach var="entry" items="${groups}">
<section class="category" id="category-${entry.key.categoryId}"><div class="section-title"><h2><c:out value="${entry.key.categoryName}"/> <small>(${counts[entry.key.categoryId]})</small></h2><span>3 video / trang</span></div>
<div class="video-grid"><c:forEach var="v" items="${entry.value}"><c:url var="videoUrl" value="/video"><c:param name="id" value="${v.videoId}"/></c:url>
<article class="video-card"><a class="poster-link" href="${videoUrl}"><img src="<c:out value='${v.poster}'/>" alt="<c:out value='${v.title}'/>" loading="lazy"><span class="poster-play" aria-hidden="true">▶</span></a><div class="card-body"><h3><a href="${videoUrl}"><c:out value="${v.title}"/></a></h3><div class="card-meta"><code>Mã: <c:out value="${v.videoId}"/></code><span><c:out value="${v.category.categoryName}"/></span></div><p class="muted">◉ ${v.views} lượt xem</p><a class="card-action" href="${videoUrl}">Xem chi tiết <span aria-hidden="true">↗</span></a></div></article>
</c:forEach><c:if test="${empty entry.value}"><p class="muted empty-state">Chưa có video ở trang này.</p></c:if></div></section></c:forEach><c:if test="${empty groups}"><p class="panel muted">Chưa có danh mục video.</p></c:if></div>
<nav class="pagination" aria-label="Phân trang"><c:if test="${page>1}"><a href="?page=${page-1}" aria-label="Trang trước">«</a></c:if><c:forEach begin="1" end="${totalPages}" var="i"><a class="${i==page?'active':''}" href="?page=${i}" aria-label="Trang ${i}" ${i==page?'aria-current="page"':''}>${i}</a></c:forEach><c:if test="${page<totalPages}"><a href="?page=${page+1}" aria-label="Trang sau">»</a></c:if></nav>
</body></html>
