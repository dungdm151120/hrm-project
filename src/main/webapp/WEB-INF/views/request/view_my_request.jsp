<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Request" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<body>
<h2>My Requests</h2>
<div style="margin-bottom: 20px;">
    <button id="btn-my" class="tab-btn active" onclick="openTab(event, 'myTab', 'btn-my')">My Requests</button>
    <button id="btn-obs" class="tab-btn" onclick="openTab(event, 'observedTab', 'btn-obs')">Observed Requests</button>
</div>

<div id="myTab" class="tab-content active">
    <div style="margin-bottom: 15px;">
        <form id="filterForm" action="view_my_request" method="GET">
          <input type="hidden" name="tab" value="my">
            <label for="status">Status:</label>
            <select name="status" id="status" onchange="document.getElementById('filterForm').submit();">
                <option value="" ${empty selectedStatus ? 'selected' : ''}>All Status</option>
                <option value="PENDING" ${selectedStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
                <option value="APPROVED" ${selectedStatus == 'APPROVED' ? 'selected' : ''}>Approved</option>
                <option value="REJECTED" ${selectedStatus == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                <option value="CANCELLED" ${selectedStatus == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                <option value="CLOSED" ${selectedStatus == 'CLOSED' ? 'selected' : ''}>Closed</option>
            </select>

            <label for="type" style="margin-left: 10px;">Type:</label>
            <select name="type" id="type" onchange="document.getElementById('filterForm').submit();">
                <option value="" ${empty selectedType ? 'selected' : ''}>All Types</option>
                <c:forEach items="<%= Request.getAllType() %>" var="entry">
                    <option value="${entry.key}" ${selectedType == entry.key ? 'selected' : ''}>
                            ${entry.value}
                    </option>
                </c:forEach>
            </select>

            <label for="sort" style="margin-left: 10px;">Sort By:</label>
            <select name="sort" id="sort" onchange="document.getElementById('filterForm').submit();">
                <option value="newest" ${selectedSort == 'newest' ? 'selected' : ''}>Newest</option>
                <option value="oldest" ${selectedSort == 'oldest' ? 'selected' : ''}>Oldest</option>
            </select>

            <a href="view_my_request?tab=my" style="margin-left: 15px;">Clear Filters</a>
        </form>
    </div>

<table border="1" cellpadding="5" cellspacing="0">
    <thead>
    <tr>
        <th>ID</th>
        <th>Proposer</th>
        <th>Type</th>
        <th>Status</th>
        <th>Created At</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${myRequests}" var="req">
        <tr>
            <td>${req.id}</td>
            <td>${req.proposerName}</td>
            <td>${req.readableType}</td>
            <td>${req.status}</td>
            <td><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
            <td>
                <a href="request_detail?id=${req.id}&from=my&status=${selectedStatus}&type=${selectedType}&sort=${selectedSort}&page=${currentPage}">View
                    Detail</a>
                <c:if test="${req.status == 'PENDING'}">
                    <form action="process_request" method="POST" style="display:inline;"
                          onsubmit="return confirm('Cancel this request?');">
                        <input type="hidden" name="requestId" value="${req.id}">
                        <input type="hidden" name="action" value="CANCEL">
                        <button type="submit">Cancel</button>
                    </form>
                </c:if>
            </td>
        </tr>
    </c:forEach>

    <c:if test="${empty myRequests}">
        <tr>
            <td colspan="6" style="text-align: center; color: gray; font-style: italic;">No requests found.</td>
        </tr>
    </c:if>
    </tbody>
</table>

<br>

<div class="pagination">
    <c:choose>
        <c:when test="${currentPage <= 1}">
            <span style="color: gray; margin-right: 5px;">Previous</span>
        </c:when>
        <c:otherwise>
            <c:url var="prevUrl" value="view_my_request">
              <c:param name="tab" value="my"/>
                <c:param name="status" value="${selectedStatus}"/>
                <c:param name="type" value="${selectedType}"/>
                <c:param name="sort" value="${selectedSort}"/>
                <c:param name="page" value="${currentPage - 1}"/>
            </c:url>
            <a href="${prevUrl}" style="margin-right: 5px;">Previous</a>
        </c:otherwise>
    </c:choose>

    <c:forEach begin="1" end="${totalPages}" var="i">
        <c:url var="pageUrl" value="view_my_request">
          <c:param name="tab" value="my"/>
            <c:param name="status" value="${selectedStatus}"/>
            <c:param name="type" value="${selectedType}"/>
            <c:param name="sort" value="${selectedSort}"/>
            <c:param name="page" value="${i}"/>
        </c:url>
        <a href="${pageUrl}"
           style="margin-right: 5px; ${currentPage == i ? 'font-weight: bold; text-decoration: underline;' : ''}">
                ${i}
        </a>
    </c:forEach>

    <c:choose>
        <c:when test="${currentPage >= totalPages}">
            <span style="color: gray;">Next</span>
        </c:when>
        <c:otherwise>
            <c:url var="nextUrl" value="view_my_request">
              <c:param name="tab" value="my"/>
                <c:param name="status" value="${selectedStatus}"/>
                <c:param name="type" value="${selectedType}"/>
                <c:param name="sort" value="${selectedSort}"/>
                <c:param name="page" value="${currentPage + 1}"/>
            </c:url>
            <a href="${nextUrl}">Next</a>
        </c:otherwise>
    </c:choose>
</div>
</div>

<div id="observedTab" class="tab-content">
  <div style="margin-bottom: 15px;">
    <form id="filterFormObs" action="view_my_request" method="GET">
      <input type="hidden" name="tab" value="obs">
      <label for="obsStatus">Status:</label>
      <select name="obsStatus" id="obsStatus" onchange="document.getElementById('filterFormObs').submit();">
        <option value="" ${empty selectedObsStatus ? 'selected' : ''}>All Status</option>
        <option value="PENDING" ${selectedObsStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
        <option value="APPROVED" ${selectedObsStatus == 'APPROVED' ? 'selected' : ''}>Approved</option>
        <option value="REJECTED" ${selectedObsStatus == 'REJECTED' ? 'selected' : ''}>Rejected</option>
        <option value="CLOSED" ${selectedObsStatus == 'CLOSED' ? 'selected' : ''}>Closed</option>
      </select>

      <label for="obsType" style="margin-left: 10px;">Type:</label>
      <select name="obsType" id="obsType" onchange="document.getElementById('filterFormObs').submit();">
        <option value="" ${empty selectedObsType ? 'selected' : ''}>All Types</option>
        <c:forEach items="<%= Request.getAllType() %>" var="entry">
          <option value="${entry.key}" ${selectedObsType == entry.key ? 'selected' : ''}>
              ${entry.value}
          </option>
        </c:forEach>
      </select>

      <label for="obsSort" style="margin-left: 10px;">Sort By:</label>
      <select name="obsSort" id="obsSort" onchange="document.getElementById('filterFormObs').submit();">
        <option value="newest" ${selectedObsSort == 'newest' ? 'selected' : ''}>Newest</option>
        <option value="oldest" ${selectedObsSort == 'oldest' ? 'selected' : ''}>Oldest</option>
      </select>

      <a href="view_my_request?tab=obs" style="margin-left: 15px;">Clear Filters</a>
    </form>
  </div>

  <table border="1" cellpadding="5" cellspacing="0">
    <thead>
    <tr>
      <th>ID</th>
      <th>Proposer</th>
      <th>Type</th>
      <th>Status</th>
      <th>Created At</th>
      <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${obsRequests}" var="req">
      <tr>
        <td>${req.id}</td>
        <td>${req.proposerName}</td>
        <td>${req.readableType}</td>
        <td>${req.status}</td>
        <td><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
        <td>
          <a href="request_detail?id=${req.id}&from=obs&obsStatus=${selectedObsStatus}&obsType=${selectedObsType}&obsSort=${selectedObsSort}&obsPage=${currentObsPage}">
            View Detail
          </a>
        </td>
      </tr>
    </c:forEach>

    <c:if test="${empty obsRequests}">
      <tr>
        <td colspan="6" style="text-align: center; color: gray; font-style: italic;">No requests found.</td>
      </tr>
    </c:if>
    </tbody>
  </table>

  <br>

  <div class="pagination">
    <c:choose>
      <c:when test="${currentObsPage <= 1}">
        <span style="color: gray; margin-right: 5px;">Previous</span>
      </c:when>
      <c:otherwise>
        <c:url var="prevUrl" value="view_my_request">
          <c:param name="tab" value="obs"/>
          <c:param name="obsStatus" value="${selectedObsStatus}"/>
          <c:param name="obsType" value="${selectedObsType}"/>
          <c:param name="obsSort" value="${selectedObsSort}"/>
          <c:param name="obsPage" value="${currentObsPage - 1}"/>
        </c:url>
        <a href="${prevUrl}" style="margin-right: 5px;">Previous</a>
      </c:otherwise>
    </c:choose>

    <c:forEach begin="1" end="${totalObsPages}" var="i">
      <c:url var="pageUrl" value="view_my_request">
        <c:param name="tab" value="obs"/>
        <c:param name="obsStatus" value="${selectedObsStatus}"/>
        <c:param name="obsType" value="${selectedObsType}"/>
        <c:param name="obsSort" value="${selectedObsSort}"/>
        <c:param name="obsPage" value="${i}"/>
      </c:url>
      <a href="${pageUrl}"
         style="margin-right: 5px; ${currentObsPage == i ? 'font-weight: bold; text-decoration: underline;' : ''}">
          ${i}
      </a>
    </c:forEach>

    <c:choose>
      <c:when test="${currentObsPage >= totalObsPages}">
        <span style="color: gray;">Next</span>
      </c:when>
      <c:otherwise>
        <c:url var="nextUrl" value="view_my_request">
          <c:param name="tab" value="obs"/>
          <c:param name="obsStatus" value="${selectedObsStatus}"/>
          <c:param name="obsType" value="${selectedObsType}"/>
          <c:param name="obsSort" value="${selectedObsSort}"/>
          <c:param name="obsPage" value="${currentObsPage + 1}"/>
        </c:url>
        <a href="${nextUrl}">Next</a>
      </c:otherwise>
    </c:choose>
  </div>
  <br>
</div>

<a href="create_request">Create New Request</a>

<script>
  function openTab(evt, tabName, btnId) {
    document.querySelectorAll('.tab-content').forEach(div => div.style.display = 'none');
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.getElementById(tabName).style.display = 'block';
    document.getElementById(btnId).classList.add('active');
  }
  // Giữ tab sau khi reload
  window.onload = () => {
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.get('tab') === 'obs') openTab(null, 'observedTab', 'btn-obs');
  };
</script>
<style>
    .tab-content {
        display: none;
    }
</style>
</body>
</html>