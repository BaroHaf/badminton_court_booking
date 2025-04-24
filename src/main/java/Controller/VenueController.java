package Controller;

import Dao.BookingDao;
import Dao.CourtDao;
import Dao.VenueDao;
import Model.Booking;
import Model.Court;
import Model.User;
import Model.Venue;
import Util.UploadImage;
import Util.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenueController {
    @WebServlet("/court-owner")
    @MultipartConfig
    public static class VenueServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            User user = (User) req.getSession().getAttribute("user");
            List<Venue> venues = new VenueDao().getAllVenuesAndCourtsByUserId(user.getId());
            req.setAttribute("venues", venues);
            req.getRequestDispatcher("/views/court_owner/venue.jsp").forward(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String name = req.getParameter("name");
            String address = req.getParameter("address");
            String openTime = req.getParameter("openTime");
            String closeTime = req.getParameter("closeTime");
            String image = UploadImage.saveImage(req, "image");
            User user = (User) req.getSession().getAttribute("user");

            VenueDao venueDao = new VenueDao();
            if (!Util.isNameValid(name)){
                req.getSession().setAttribute("warning", "Tên sân không hợp lệ. Chỉ chấp nhận chữ, số, khoảng trắng, dấu gạch ngang hoặc gạch dưới, độ dài 2-100 ký tự..");
                resp.sendRedirect(req.getContextPath() + "/court-owner");
                return;
            }
            // Check if address already exists
            if (venueDao.isAddressExists(address)) {
                req.getSession().setAttribute("warning", "Địa chỉ đã tồn tại. Vui lòng chọn địa chỉ khác.");
                resp.sendRedirect(req.getContextPath() + "/court-owner");
                return;
            }

            Venue venue = new Venue(name, address, image, LocalTime.parse(openTime), LocalTime.parse(closeTime), user);
            venueDao.save(venue);
            req.getSession().setAttribute("success", "Thêm mới thành công.");
            resp.sendRedirect(req.getContextPath() + "/court-owner");
        }
    }

    @WebServlet("/court-owner/detail")
    @MultipartConfig
    public static class VenueDetailServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            int venueId = Integer.parseInt(req.getParameter("id"));
            User user = (User) req.getSession().getAttribute("user");
            Venue venue = new VenueDao().getVenueByUserIdAndVenueId(user.getId(), venueId);
            req.setAttribute("venue", venue);
            req.getRequestDispatcher("/views/court_owner/venue-detail.jsp").forward(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            long id = Long.parseLong(req.getParameter("id"));
            Venue venue = new VenueDao().getById(id);

            if (venue == null) {
                req.getSession().setAttribute("warning", "Sân không tồn tại");
            } else {
                String name = req.getParameter("name");
                String address = req.getParameter("address");
                String openTime = req.getParameter("openTime");
                String closeTime = req.getParameter("closeTime");

                // Validate name
                if (!Util.isNameValid(name)) {
                    req.getSession().setAttribute("warning", "Tên sân không hợp lệ. Chỉ chấp nhận chữ, số, khoảng trắng, dấu gạch ngang hoặc gạch dưới, độ dài 2-100 ký tự..");
                    resp.sendRedirect(req.getContextPath() + "/court-owner/detail");
                    return;
                }

                // Check if anything has changed
                boolean isUpdated = false;

                // Check if the name has changed
                if (!venue.getName().equals(name)) {
                    venue.setName(name);
                    isUpdated = true;
                }

                // Check if the address has changed
                if (!venue.getAddress().equals(address)) {
                    venue.setAddress(address);
                    isUpdated = true;
                }

                // Check if the open time has changed
                if (!venue.getOpenTime().equals(LocalTime.parse(openTime))) {
                    venue.setOpenTime(LocalTime.parse(openTime));
                    isUpdated = true;
                }

                // Check if the close time has changed
                if (!venue.getCloseTime().equals(LocalTime.parse(closeTime))) {
                    venue.setCloseTime(LocalTime.parse(closeTime));
                    isUpdated = true;
                }

                // If no changes were made
                if (!isUpdated) {
                    req.getSession().setAttribute("warning", "Không có gì thay đổi, vui lòng điền thông tin.");
                } else {
                    // If there are changes, proceed with the update
                    if (req.getPart("image") != null && req.getPart("image").getSize() > 0) {
                        String image = UploadImage.saveImage(req, "image");
                        venue.setImage(image);
                    }
                    new VenueDao().update(venue);
                    req.getSession().setAttribute("success", "Cập nhật thành công");
                }
            }
            resp.sendRedirect(req.getContextPath() + "/court-owner/detail?id=" + id);
        }

    }

    @WebServlet("/court-owner/court")
    public static class CourtServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String court_id = req.getParameter("court_id");
            Court court = new CourtDao().getById(Long.parseLong(court_id));
            if (court == null) {
                req.getSession().setAttribute("warning", "Sân không tồn tại");
            } else {
                court.setAvailable(!court.isAvailable());
                new CourtDao().update(court);
            }
            resp.sendRedirect(req.getHeader("Referer"));
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String name = req.getParameter("name");
            boolean isAvailable = Boolean.parseBoolean(req.getParameter("isAvailable"));
            double pricePerHour = Double.parseDouble(req.getParameter("pricePerHour"));
            long venueId = Long.parseLong(req.getParameter("venueId"));
            Venue venue = new VenueDao().getById(venueId);

            if (venue == null) {
                req.getSession().setAttribute("warning", "Sân không tồn tại");
            } else {
                Court check = new CourtDao().findByName(name, venueId);
                if (check != null) {
                    req.getSession().setAttribute("warning", "Số sân đã tồn tại.");
                } else if (pricePerHour < 10000) {
                    req.getSession().setAttribute("warning", "Giá quá thấp. Giá phải từ 10,000 trở lên.");
                } else {
                    Court court = new Court();
                    court.setName(name);
                    court.setPricePerHour(pricePerHour);
                    court.setVenue(venue);
                    court.setAvailable(isAvailable);
                    court.setDeleted(false);
                    new CourtDao().save(court);
                    req.getSession().setAttribute("success", "Thêm sân thành công.");
                }
            }
            resp.sendRedirect(req.getContextPath() + "/court-owner/detail?id=" + venueId);
        }

        @WebServlet("/venue-detail")
        public static class ViewVenueDetailServlet extends HttpServlet {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                long venueId = Long.parseLong(req.getParameter("id"));
                Venue venue = new VenueDao().findByIdAndDeletedFalse(venueId);
                if (venue != null) {
                    String from = req.getParameter("from");
                    String to = req.getParameter("to");
                    LocalDateTime startDateTime;
                    LocalDateTime endDateTime;
                    if (from.isEmpty() && to.isEmpty()) {
                        LocalDate today = LocalDate.now();
                        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
                        startDateTime = startOfWeek.atStartOfDay();
                        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
                        endDateTime = endOfWeek.atTime(23, 59);
                    } else {
                        LocalDate startDate = LocalDate.parse(from);
                        LocalDate endDate = LocalDate.parse(to);
                        startDateTime = startDate.atStartOfDay();
                        endDateTime = endDate.atTime(23, 59);
                    }
                    List<Booking> bookings = new BookingDao().getConfirmedBookingIn(startDateTime, endDateTime);
                    Map<Double, List<String>> courtPriceMap = new HashMap<>();
                    for (Court court : venue.getCourts()) {
                        double price = court.getPricePerHour();
                        String courtName = court.getName();

                        courtPriceMap.computeIfAbsent(price, k -> new ArrayList<>()).add(courtName);
                    }
                    req.setAttribute("courtPriceMap", courtPriceMap);
                    req.setAttribute("venue", venue);
                    req.setAttribute("bookings", bookings);
                    req.getRequestDispatcher("/views/public/venue-detail.jsp").forward(req, resp);
                } else {
                    req.getSession().setAttribute("warning", "Sân không tồn tại hoặc đã bị xóa");
                    resp.sendRedirect(req.getContextPath() + "/");
                }
            }
        }

        @WebServlet("/search")
        public static class SearchServlet extends HttpServlet {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                String searchQuery = req.getParameter("searchQuery");
                String openTimeStr = req.getParameter("openTime");
                String closeTimeStr = req.getParameter("closeTime");

                List<Venue> venues;

                LocalTime openTime = null;
                LocalTime closeTime = null;

                try {
                    if (openTimeStr != null && !openTimeStr.trim().isEmpty()) {
                        openTime = LocalTime.parse(openTimeStr);
                    }

                    if (closeTimeStr != null && !closeTimeStr.trim().isEmpty()) {
                        closeTime = LocalTime.parse(closeTimeStr);
                    }
                } catch (DateTimeParseException e) {
                    req.getSession().setAttribute("warning", "Định dạng thời gian không hợp lệ.");
                    req.setAttribute("venues", new ArrayList<>());
                    req.getRequestDispatcher("/views/public/search.jsp").forward(req, resp);
                    return;
                }

                // Dù có hoặc không có open/close time, vẫn gọi search
                venues = new VenueDao().searchVenues(searchQuery, openTime, closeTime);
                req.setAttribute("venues", venues != null ? venues : new ArrayList<>());
                req.getRequestDispatcher("/views/public/search.jsp").forward(req, resp);
            }

        }

        @WebServlet("/court-owner/delete-venue")
        public static class DeleteVenueServlet extends HttpServlet {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                long venueId = Long.parseLong(req.getParameter("id"));
                VenueDao venueDao = new VenueDao();
                Venue venue = venueDao.getById(venueId);
                if (venue != null) {
                    venue.setDeleted(true);
                    venueDao.update(venue);
                    req.getSession().setAttribute("success", "Đã xóa thành công.");
                } else {
                    req.getSession().setAttribute("warning", "Không tìm thấy sân.");
                }
                resp.sendRedirect(req.getHeader("referer"));
            }
        }

    /*@WebServlet("/court-owner/update-venue")
    @MultipartConfig
    public static class UpdateVenueServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            long id = Long.parseLong(req.getParameter("id"));
            VenueDao venueDao = new VenueDao();
            Venue venue = venueDao.getById(id);
            if (venue != null) {
                String name = req.getParameter("name");
                String address = req.getParameter("address");
                LocalTime openTime = LocalTime.parse(req.getParameter("openTime"));
                LocalTime closeTime = LocalTime.parse(req.getParameter("closeTime"));
                venue.setName(name);
                venue.setNormalizedName(Util.removeAccents(name));
                venue.setAddress(address);
                venue.setNormalizedAddress(Util.removeAccents(address));
                venue.setOpenTime(openTime);
                venue.setCloseTime(closeTime);
                if (req.getPart("image") != null) {
                    String newFileName = UploadImage.saveImage(req, "image");
                    venue.setImage(newFileName);
                }
                venueDao.update(venue);
                req.getSession().setAttribute("success", "Cập nhật thành công.");
            } else {
                req.getSession().setAttribute("warning", "Id không tồn tại.");
            }
            resp.sendRedirect(req.getHeader("referer"));
        }
    }*/

        @WebServlet("/court-owner/delete-court")
        public static class DeleteCourtServlet extends HttpServlet {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                long courtId = Long.parseLong(req.getParameter("deleteCourtId"));
                CourtDao courtDao = new CourtDao();
                Court court = courtDao.getById(courtId);
                if (court != null) {
                    court.setDeleted(true);
                    courtDao.update(court);
                    req.getSession().setAttribute("success", "Đã xóa thành công.");
                } else {
                    req.getSession().setAttribute("warning", "Không tìm thấy sân.");
                }
                resp.sendRedirect(req.getHeader("referer"));
            }
        }

        @WebServlet("/court-owner/update-court")
        public static class UpdateCourtServlet extends HttpServlet {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                long courtId = Long.parseLong(req.getParameter("updateCourtId"));
                CourtDao courtDao = new CourtDao();
                Court court = courtDao.getById(courtId);
                if (court != null) {
                    court.setName(req.getParameter("name"));
                    court.setPricePerHour(Double.parseDouble(req.getParameter("pricePerHour")));
                    new CourtDao().update(court);
                    req.getSession().setAttribute("success", "Cập nhật thành công.");
                } else {
                    req.getSession().setAttribute("warning", "Không tìm thấy sân.");
                }
                resp.sendRedirect(req.getHeader("referer"));
            }
        }
    }
}
