package org.nanoboot.starter;

/**
 * 控制台颜色工具类 - 使用 ANSI 转义序列控制终端颜色
 * 无需引入任何第三方库
 */
public class ConsoleColor {
    // 重置所有样式（颜色、加粗等）
    public static final String RESET = "\033[0m";

    // 字体颜色（前景色）
    public static final String BLACK = "\033[30m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String PURPLE = "\033[35m";
    public static final String CYAN = "\033[36m";
    public static final String WHITE = "\033[37m";

    // 背景色
    public static final String BG_BLACK = "\033[40m";
    public static final String BG_RED = "\033[41m";
    public static final String BG_GREEN = "\033[42m";
    public static final String BG_YELLOW = "\033[43m";

    // 样式增强
    public static final String BOLD = "\033[1m";       // 加粗
    public static final String UNDERLINE = "\033[4m";  // 下划线

    // 封装常用的日志样式方法，简化使用
    public static String info(String msg) {
        return BLUE + "[INFO] " + RESET + msg;
    }

    public static String success(String msg) {
        return GREEN + BOLD + "[SUCCESS] " + RESET + msg;
    }

    public static String warning(String msg) {
        return YELLOW + "[WARNING] " + RESET + msg;
    }

    public static String error(String msg) {
        return RED + BOLD + "[ERROR] " + RESET + msg;
    }

    // 测试示例
    public static void main(String[] args) {
        System.out.println(info("程序启动成功"));
        System.out.println(success("数据加载完成"));
        System.out.println(warning("内存使用率超过80%"));
        System.out.println(error("数据库连接失败"));

        // 自定义样式（前景红+背景黄+加粗）
        String customMsg = BOLD + RED + BG_YELLOW
                + " 紧急告警 " + RESET + " 系统即将重启";
        System.out.println(customMsg);
    }
}
