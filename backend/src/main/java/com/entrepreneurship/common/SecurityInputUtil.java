package com.entrepreneurship.common;

import com.entrepreneurship.dto.LoginDTO;
import com.entrepreneurship.dto.ProjectDTO;
import com.entrepreneurship.dto.RegisterDTO;
import com.entrepreneurship.entity.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public final class SecurityInputUtil {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]{1,190}\\.[A-Za-z]{2,20}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^$|^1[3-9]\\d{9}$|^\\+?[0-9\\- ]{6,20}$");
    private static final Set<String> ROLES = new HashSet<>(Arrays.asList("student", "mentor", "investor", "admin"));
    private static final Set<String> STATUSES = new HashSet<>(Arrays.asList(
            "pending", "approved", "rejected", "active", "inactive", "completed",
            "confirmed", "registered", "cancelled", "replied", "processed", "processing", "resolved",
            "closed", "read", "unread", "upcoming", "ongoing", "finished",
            "funding", "funded", "incubating", "draft", "disabled"
    ));

    private SecurityInputUtil() {
    }

    public static Long requirePositiveId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BusinessException(400, fieldName + "不合法");
        }
        return id;
    }

    public static int page(int page) {
        return Math.max(page, 1);
    }

    public static int size(int size) {
        if (size < 1) {
            return 10;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    public static String requireText(String value, int maxLength, String fieldName) {
        String cleaned = cleanText(value, maxLength, fieldName);
        if (cleaned == null || cleaned.isEmpty()) {
            throw new BusinessException(400, fieldName + "不能为空");
        }
        return cleaned;
    }

    public static String cleanText(String value, int maxLength, String fieldName) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new BusinessException(400, fieldName + "长度不能超过" + maxLength);
        }
        for (int i = 0; i < trimmed.length(); i++) {
            char ch = trimmed.charAt(i);
            if (Character.isISOControl(ch) && ch != '\n' && ch != '\r' && ch != '\t') {
                throw new BusinessException(400, fieldName + "包含非法控制字符");
            }
        }
        return escapeHtml(trimmed);
    }

    public static String cleanUsername(String username) {
        String cleaned = requireText(username, 30, "用户名");
        if (!USERNAME_PATTERN.matcher(cleaned).matches()) {
            throw new BusinessException(400, "用户名仅支持3-30位字母、数字或下划线");
        }
        return cleaned;
    }

    public static String cleanPassword(String password, String fieldName) {
        if (password == null || password.length() < 6 || password.length() > 64) {
            throw new BusinessException(400, fieldName + "长度必须为6-64位");
        }
        return password;
    }

    public static String cleanEmail(String email) {
        String cleaned = cleanText(email, 255, "邮箱");
        if (cleaned != null && !cleaned.isEmpty() && !EMAIL_PATTERN.matcher(cleaned).matches()) {
            throw new BusinessException(400, "邮箱格式不正确");
        }
        return cleaned;
    }

    public static String cleanPhone(String phone) {
        String cleaned = cleanText(phone, 20, "手机号");
        if (cleaned != null && !PHONE_PATTERN.matcher(cleaned).matches()) {
            throw new BusinessException(400, "手机号格式不正确");
        }
        return cleaned;
    }

    public static String cleanRole(String role) {
        String cleaned = requireText(role, 20, "角色");
        if (!ROLES.contains(cleaned)) {
            throw new BusinessException(400, "角色不合法");
        }
        return cleaned;
    }

    public static String cleanStatus(String status) {
        String cleaned = cleanText(status, 30, "状态");
        if (cleaned != null && !cleaned.isEmpty() && !STATUSES.contains(cleaned)) {
            throw new BusinessException(400, "状态不合法");
        }
        return cleaned;
    }

    public static String cleanUrl(String value, int maxLength, String fieldName) {
        String cleaned = cleanText(value, maxLength, fieldName);
        if (cleaned == null || cleaned.isEmpty()) {
            return cleaned;
        }
        String lower = cleaned.toLowerCase();
        if (lower.startsWith("javascript:") || lower.startsWith("data:") || lower.startsWith("file:")
                || lower.contains("..") || lower.contains("%2e") || lower.contains("\\")) {
            throw new BusinessException(400, fieldName + "不合法");
        }
        if (!(lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("/"))) {
            throw new BusinessException(400, fieldName + "必须为http(s)地址或站内路径");
        }
        return cleaned;
    }

    public static BigDecimal positiveAmount(BigDecimal amount, String fieldName) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, fieldName + "必须大于0");
        }
        return amount;
    }

    public static void requireRole(User user, String role) {
        if (user == null || !role.equals(user.getRole())) {
            throw new BusinessException(403, "无权限访问");
        }
    }

    public static void sanitize(LoginDTO dto) {
        if (dto == null) {
            throw new BusinessException(400, "登录参数不能为空");
        }
        dto.setUsername(cleanUsername(dto.getUsername()));
        dto.setPassword(cleanPassword(dto.getPassword(), "密码"));
    }

    public static void sanitize(RegisterDTO dto) {
        if (dto == null) {
            throw new BusinessException(400, "注册参数不能为空");
        }
        dto.setUsername(cleanUsername(dto.getUsername()));
        dto.setPassword(cleanPassword(dto.getPassword(), "密码"));
        dto.setName(cleanText(dto.getName(), 50, "姓名"));
        dto.setEmail(cleanEmail(dto.getEmail()));
        dto.setPhone(cleanPhone(dto.getPhone()));
        dto.setRole(cleanRole(dto.getRole()));
        dto.setCompany(cleanText(dto.getCompany(), 120, "投资机构"));
    }

    public static void sanitize(ProjectDTO dto) {
        if (dto == null) {
            throw new BusinessException(400, "项目参数不能为空");
        }
        dto.setTitle(requireText(dto.getTitle(), 100, "项目名称"));
        dto.setDescription(cleanText(dto.getDescription(), 2000, "项目描述"));
        dto.setCategory(cleanText(dto.getCategory(), 50, "项目分类"));
        dto.setTeamInfo(cleanText(dto.getTeamInfo(), 2000, "团队信息"));
        dto.setBusinessPlan(cleanText(dto.getBusinessPlan(), 5000, "商业计划"));
        if (dto.getFundingTarget() != null) dto.setFundingTarget(positiveAmount(dto.getFundingTarget(), "融资金额"));
        if (dto.getTeamSize() != null && (dto.getTeamSize() < 1 || dto.getTeamSize() > 1000)) {
            throw new BusinessException(400, "团队规模必须为1-1000");
        }
    }

    public static void sanitizeOptional(ProjectDTO dto) {
        if (dto == null) {
            throw new BusinessException(400, "项目参数不能为空");
        }
        if (dto.getTitle() != null) dto.setTitle(requireText(dto.getTitle(), 100, "项目名称"));
        dto.setDescription(cleanText(dto.getDescription(), 2000, "项目描述"));
        dto.setCategory(cleanText(dto.getCategory(), 50, "项目分类"));
        dto.setTeamInfo(cleanText(dto.getTeamInfo(), 2000, "团队信息"));
        dto.setBusinessPlan(cleanText(dto.getBusinessPlan(), 5000, "商业计划"));
        if (dto.getFundingTarget() != null) dto.setFundingTarget(positiveAmount(dto.getFundingTarget(), "融资金额"));
        if (dto.getTeamSize() != null && (dto.getTeamSize() < 1 || dto.getTeamSize() > 1000)) {
            throw new BusinessException(400, "团队规模必须为1-1000");
        }
    }

    public static void sanitize(User user) {
        if (user == null) {
            throw new BusinessException(400, "用户参数不能为空");
        }
        user.setName(cleanText(user.getName(), 50, "姓名"));
        user.setEmail(cleanEmail(user.getEmail()));
        user.setPhone(cleanPhone(user.getPhone()));
        user.setAvatar(cleanUrl(user.getAvatar(), 500, "头像"));
        user.setPassword(null);
        user.setRole(null);
        user.setStatus(null);
    }

    public static void sanitize(MentorInfo info) {
        if (info == null) throw new BusinessException(400, "导师信息不能为空");
        info.setExpertise(cleanText(info.getExpertise(), 500, "擅长领域"));
        info.setIntroduction(cleanText(info.getIntroduction(), 2000, "简介"));
        info.setAvailability(cleanText(info.getAvailability(), 500, "可用时间"));
    }

    public static void sanitize(InvestorInfo info) {
        if (info == null) throw new BusinessException(400, "投资人信息不能为空");
        info.setCompany(cleanText(info.getCompany(), 120, "公司"));
        info.setPosition(cleanText(info.getPosition(), 80, "职位"));
        info.setInvestmentField(cleanText(info.getInvestmentField(), 500, "投资领域"));
        info.setBudget(cleanText(info.getBudget(), 100, "投资预算"));
        info.setBio(cleanText(info.getBio(), 2000, "简介"));
        info.setAvatar(cleanUrl(info.getAvatar(), 500, "头像"));
    }

    public static void sanitize(Feedback feedback) {
        if (feedback == null) throw new BusinessException(400, "反馈参数不能为空");
        feedback.setType(cleanText(feedback.getType(), 50, "反馈类型"));
        feedback.setTitle(cleanText(feedback.getTitle(), 200, "反馈标题"));
        feedback.setContent(requireText(feedback.getContent(), 2000, "反馈内容"));
        feedback.setStatus(cleanStatus(feedback.getStatus()));
        feedback.setReply(cleanText(feedback.getReply(), 2000, "回复内容"));
    }

    public static void sanitize(Message message) {
        if (message == null) throw new BusinessException(400, "消息参数不能为空");
        requirePositiveId(message.getReceiverId(), "接收用户");
        message.setContent(requireText(message.getContent(), 2000, "消息内容"));
    }

    public static void sanitize(Consultation consultation) {
        if (consultation == null) throw new BusinessException(400, "咨询参数不能为空");
        requirePositiveId(consultation.getMentorId(), "导师");
        consultation.setTopic(cleanText(consultation.getTopic(), 100, "咨询主题"));
        consultation.setContent(requireText(consultation.getContent(), 2000, "咨询内容"));
        consultation.setStatus(cleanStatus(consultation.getStatus()));
        consultation.setNotes(cleanText(consultation.getNotes(), 2000, "备注"));
    }

    public static void sanitizeConsultationOptional(Consultation consultation) {
        if (consultation == null) throw new BusinessException(400, "咨询参数不能为空");
        if (consultation.getMentorId() != null) requirePositiveId(consultation.getMentorId(), "导师");
        if (consultation.getProjectId() != null) requirePositiveId(consultation.getProjectId(), "项目");
        if (consultation.getTopic() != null) consultation.setTopic(requireText(consultation.getTopic(), 100, "咨询主题"));
        consultation.setContent(cleanText(consultation.getContent(), 2000, "咨询内容"));
        consultation.setStatus(cleanStatus(consultation.getStatus()));
        consultation.setNotes(cleanText(consultation.getNotes(), 2000, "备注"));
    }

    public static void sanitize(Training training) {
        if (training == null) throw new BusinessException(400, "培训参数不能为空");
        training.setTitle(requireText(training.getTitle(), 100, "培训标题"));
        training.setDescription(cleanText(training.getDescription(), 2000, "培训描述"));
        training.setInstructor(cleanText(training.getInstructor(), 80, "讲师"));
        training.setLocation(cleanText(training.getLocation(), 200, "地点"));
        if (training.getMaxParticipants() != null && training.getMaxParticipants() < 1) {
            throw new BusinessException(400, "最大参与人数必须大于0");
        }
        training.setStatus(cleanStatus(training.getStatus()));
        training.setCoverImage(cleanUrl(training.getCoverImage(), 500, "封面图片"));
    }

    public static void sanitizeTrainingOptional(Training training) {
        if (training == null) throw new BusinessException(400, "培训参数不能为空");
        if (training.getTitle() != null) training.setTitle(requireText(training.getTitle(), 100, "培训标题"));
        training.setDescription(cleanText(training.getDescription(), 2000, "培训描述"));
        training.setInstructor(cleanText(training.getInstructor(), 80, "讲师"));
        training.setLocation(cleanText(training.getLocation(), 200, "地点"));
        if (training.getMaxParticipants() != null && training.getMaxParticipants() < 1) {
            throw new BusinessException(400, "最大参与人数必须大于0");
        }
        training.setStatus(cleanStatus(training.getStatus()));
        training.setCoverImage(cleanUrl(training.getCoverImage(), 500, "封面图片"));
    }

    public static void sanitize(Roadshow roadshow) {
        if (roadshow == null) throw new BusinessException(400, "路演参数不能为空");
        roadshow.setTitle(requireText(roadshow.getTitle(), 100, "路演标题"));
        roadshow.setDescription(cleanText(roadshow.getDescription(), 2000, "路演描述"));
        roadshow.setLocation(cleanText(roadshow.getLocation(), 200, "地点"));
        if (roadshow.getMaxProjects() != null && roadshow.getMaxProjects() < 1) {
            throw new BusinessException(400, "最大项目数必须大于0");
        }
        roadshow.setStatus(cleanStatus(roadshow.getStatus()));
        roadshow.setCoverImage(cleanUrl(roadshow.getCoverImage(), 500, "封面图片"));
    }

    public static void sanitizeRoadshowOptional(Roadshow roadshow) {
        if (roadshow == null) throw new BusinessException(400, "路演参数不能为空");
        if (roadshow.getTitle() != null) roadshow.setTitle(requireText(roadshow.getTitle(), 100, "路演标题"));
        roadshow.setDescription(cleanText(roadshow.getDescription(), 2000, "路演描述"));
        roadshow.setLocation(cleanText(roadshow.getLocation(), 200, "地点"));
        if (roadshow.getMaxProjects() != null && roadshow.getMaxProjects() < 1) {
            throw new BusinessException(400, "最大项目数必须大于0");
        }
        roadshow.setStatus(cleanStatus(roadshow.getStatus()));
        roadshow.setCoverImage(cleanUrl(roadshow.getCoverImage(), 500, "封面图片"));
    }

    public static void sanitize(RoadshowProject roadshowProject) {
        if (roadshowProject == null) throw new BusinessException(400, "路演项目参数不能为空");
        requirePositiveId(roadshowProject.getProjectId(), "项目");
        requirePositiveId(roadshowProject.getPresenterId(), "展示人");
        if (roadshowProject.getOrderNum() != null && roadshowProject.getOrderNum() < 0) {
            throw new BusinessException(400, "展示顺序不合法");
        }
        roadshowProject.setStatus(cleanStatus(roadshowProject.getStatus()));
        roadshowProject.setVideoUrl(cleanUrl(roadshowProject.getVideoUrl(), 500, "视频地址"));
    }

    public static void sanitize(Investment investment) {
        if (investment == null) throw new BusinessException(400, "投资参数不能为空");
        requirePositiveId(investment.getProjectId(), "项目");
        investment.setAmount(positiveAmount(investment.getAmount(), "投资金额"));
        investment.setStatus(cleanStatus(investment.getStatus()));
    }

    public static String sanitizeBlockchainType(String type) {
        return requireText(type, 50, "记录类型");
    }

    public static String sanitizeBlockchainData(String data) {
        return requireText(data, 5000, "记录内容");
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
