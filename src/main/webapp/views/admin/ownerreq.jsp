```jsp
<%@ page import="java.util.List" %>
<%@ page import="Model.OwnerReq" %>
<%@ page import="Model.Constant.Status" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <%@include file="../include/head.jsp"%>
    <title>Duyệt Owner</title>
</head>

<body>

<!-- ======= Header ======= -->
<%@include file="../include/header.jsp"%>
<!-- End Header -->

<!-- ======= Sidebar ======= -->
<%@include file="../include/sidebar.jsp"%>
<!-- End Sidebar-->

<main id="main" class="main">

    <!-- ======= Title ======= -->
    <div class="pagetitle">
        <h1>Quản lý yêu cầu Owner</h1>
        <nav>
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="<%=request.getContextPath()%>/">Home</a></li>
                <li class="breadcrumb-item active">Duyệt Owner</li>
            </ol>
        </nav>
    </div>
    <!-- End Page Title -->

    <section class="section dashboard">
        <div class="row">
            <div class="col-12">
                <h2 class="text-center">Danh sách yêu cầu Owner</h2>
                <table id="myTable" class="table table-striped table-bordered">
                    <thead class="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        List<OwnerReq> ownerRequests = (List<OwnerReq>) request.getAttribute("ownerRequests");
                        if (ownerRequests != null && !ownerRequests.isEmpty()) {
                            for (OwnerReq req : ownerRequests) {
                    %>
                    <tr>
                        <td><%= req.getId() %></td>
                        <td><%= req.getUser_name() %></td>
                        <td><%= req.getStatus() %></td>
                        <td>
                            <% if (req.getStatus() == Status.PENDING) { %>
                            <form action="<%=request.getContextPath()%>/admin/ownerrequest" method="post">
                                <input type="hidden" name="username" value="<%=req.getUser_name()%>">
                                <button type="submit" name="action" value="ACCEPTED" class="btn btn-success btn-sm">Duyệt</button>
                            </form>
                            <form action="<%=request.getContextPath()%>/admin/ownerrequest" method="post" class="d-inline">
                                <input type="hidden" name="username" value="<%= req.getUser_name() %>">
                                <button type="submit" name="action" value="REJECTED" class="btn btn-danger btn-sm">Từ chối</button>
                            </form>
                            <% } %>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="4" class="text-center">Không có yêu cầu nào.</td>
                    </tr>
                    <%
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

</main>
<!-- End #main -->

<!-- ======= Footer ======= -->
<%@include file="../include/footer.jsp"%>
<!-- End Footer -->

<a href="#" class="back-to-top d-flex align-items-center justify-content-center"><i class="bi bi-arrow-up-short"></i></a>
<%@include file="../include/js.jsp"%>
<script>
    $("#myTable").DataTable({
        searching: false, // Tắt thanh tìm kiếm
        paging: true,     // Bật phân trang
        ordering: true,   // Bật sắp xếp cột
        info: true        // Hiển thị thông tin số lượng bản ghi
    });
</script>
</body>

</html>
```