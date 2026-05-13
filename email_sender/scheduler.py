"""
邮件推送自动化触发模块 - M2
定时任务脚本，与简报生成流程打通
技术栈: Python + SendGrid + APScheduler (可选)
"""

import os
import sys
import argparse
from datetime import datetime, timedelta
from dataclasses import dataclass
from typing import List, Optional

# 尝试导入 APScheduler（可选）
try:
    from apscheduler.schedulers.blocking import BlockingScheduler
    from apscheduler.triggers.cron import CronTrigger
    SCHEDULER_AVAILABLE = True
except ImportError:
    SCHEDULER_AVAILABLE = False

from sender import EmailSender, EmailMessage, build_daily_brief_email


@dataclass
class Subscriber:
    """订阅者"""
    email: str
    name: str = ""
    active: bool = True


# ---------------------------------------------------------------------------
# 配置
# ---------------------------------------------------------------------------

SUBSCRIBERS = [
    Subscriber(email="test@example.com", name="测试用户"),
    # 添加更多订阅者
]

# 每日简报生成时间（UTC），默认 09:00 北京时间 17:00
DAILY_BRIEF_CRON_HOUR = 9
DAILY_BRIEF_CRON_MINUTE = 0

# FROM_EMAIL 默认值
DEFAULT_FROM_EMAIL = os.environ.get("FROM_EMAIL", "noreply@example.com")

# Mock 模式（本地测试用）
MOCK_MODE = os.environ.get("MOCK_MODE", "false").lower() == "true"


# ---------------------------------------------------------------------------
# 简报内容获取（待与简报生成流程打通）
# ---------------------------------------------------------------------------

def fetch_daily_brief_content(date: str = None) -> str:
    """
    获取每日简报内容。
    目前为占位实现，实际应与简报生成系统对接。
    """
    if date is None:
        date = datetime.now().strftime("%Y-%m-%d")

    # TODO: 实际应从简报系统/数据库获取内容
    placeholder_content = f"""
    <h3>每日简报 {date}</h3>
    <p>以下是今日的 AI、金融、政治要点：</p>
    <ul>
        <li><strong>AI：</strong>最新AI进展摘要...</li>
        <li><strong>金融：</strong>市场动态...</li>
        <li><strong>政治：</strong>重要新闻...</li>
    </ul>
    <p>详细信息请访问系统查看。</p>
    """
    return placeholder_content


# ---------------------------------------------------------------------------
# 邮件发送
# ---------------------------------------------------------------------------

def send_daily_brief(sender: EmailSender, content: str, date: str = None) -> dict:
    """
    向所有活跃订阅者发送每日简报
    """
    results = []
    sent_at = datetime.utcnow().isoformat() + "Z"

    for subscriber in SUBSCRIBERS:
        if not subscriber.active:
            continue

        email = build_daily_brief_email(
            recipient=subscriber.email,
            content=content,
            date=date
        )

        if MOCK_MODE:
            email.sent_at = sent_at
            email.status = "sent"
            results.append({
                "recipient": subscriber.email,
                "status": "sent (mock)",
                "sent_at": sent_at
            })
            continue

        try:
            response = sender.send(email)
            results.append({
                "recipient": subscriber.email,
                "status": email.status,
                "sent_at": email.sent_at,
                "response": response.get("status_code")
            })
        except Exception as e:
            results.append({
                "recipient": subscriber.email,
                "status": "failed",
                "error": str(e)
            })

    return {
        "date": date or datetime.now().strftime("%Y-%m-%d"),
        "sent_at": sent_at,
        "total": len(SUBSCRIBERS),
        "active": sum(1 for s in SUBSCRIBERS if s.active),
        "results": results
    }


# ---------------------------------------------------------------------------
# CLI 入口
# ---------------------------------------------------------------------------

def main():
    parser = argparse.ArgumentParser(description="每日简报邮件推送定时任务")
    parser.add_argument("--once", action="store_true", help="立即发送一封邮件（手动触发）")
    parser.add_argument("--daemon", action="store_true", help="以后台定时任务模式运行")
    parser.add_argument("--hour", type=int, default=DAILY_BRIEF_CRON_HOUR, help=f"每日发送小时（UTC，默认 {DAILY_BRIEF_CRON_HOUR}）")
    parser.add_argument("--minute", type=int, default=DAILY_BRIEF_CRON_MINUTE, help=f"每日发送分钟（默认 {DAILY_BRIEF_CRON_MINUTE}）")
    args = parser.parse_args()

    api_key = os.environ.get("SENDGRID_API_KEY")
    if not api_key and not MOCK_MODE:
        print("错误: 请设置环境变量 SENDGRID_API_KEY，或设置 MOCK_MODE=true")
        sys.exit(1)

    sender = EmailSender(api_key) if not MOCK_MODE else None
    date_str = datetime.now().strftime("%Y-%m-%d")
    content = fetch_daily_brief_content(date_str)

    if args.once:
        # 手动触发一次
        print(f"手动触发邮件推送 (MOCK_MODE={'true' if MOCK_MODE else 'false'})")
        result = send_daily_brief(sender, content, date_str)
        print(f"发送结果: {result}")
        return

    if args.daemon:
        if not SCHEDULER_AVAILABLE:
            print("错误: 请安装 APScheduler: pip install apscheduler")
            sys.exit(1)
        print(f"启动定时任务，每天 {args.hour}:{args.minute:02d} UTC 发送邮件")
        scheduler = BlockingScheduler()
        scheduler.add_job(
            send_daily_brief,
            CronTrigger(hour=args.hour, minute=args.minute),
            args=[sender, content, date_str],
            id="daily_brief_email"
        )
        scheduler.start()
        return

    # 无参数：打印使用说明
    parser.print_help()


if __name__ == "__main__":
    main()