"""
邮件推送模块 - M1: SendGrid SDK 集成与单封邮件发送验证
技术栈: Python + SendGrid
"""

import os
from datetime import datetime
from dataclasses import dataclass
from typing import Optional

# SendGrid SDK
try:
    from sendgrid import SendGridAPIClient
    from sendgrid.helpers.mail import Mail
except ImportError:
    raise ImportError("请安装 sendgrid: pip install sendgrid")


@dataclass
class EmailMessage:
    """邮件字段模型"""
    recipient_email: str
    subject: str
    body: str
    sent_at: Optional[str] = None
    status: str = "pending"  # pending / sent / failed


class EmailSender:
    """邮件发送服务"""

    def __init__(self, api_key: str = None):
        self.api_key = api_key or os.environ.get("SENDGRID_API_KEY")
        if not self.api_key:
            raise ValueError("SENDGRID_API_KEY 未设置")
        self.client = SendGridAPIClient(api_key)

    def send(self, email: EmailMessage) -> dict:
        """
        发送邮件
        :param email: EmailMessage 实例
        :return: SendGrid API 响应
        """
        message = Mail(
            from_email=os.environ.get("FROM_EMAIL", "noreply@example.com"),
            to_emails=email.recipient_email,
            subject=email.subject,
            html_content=email.body
        )

        response = self.client.send(message)

        email.sent_at = datetime.utcnow().isoformat() + "Z"
        email.status = "sent" if response.status_code == 202 else "failed"

        return {
            "status_code": response.status_code,
            "body": response.body.decode() if response.body else "",
            "sent_at": email.sent_at
        }


def build_daily_brief_email(recipient: str, content: str, date: str = None) -> EmailMessage:
    """
    构建每日简报邮件
    :param recipient: 接收人邮箱
    :param content: 简报正文（HTML）
    :param date: 日期，默认今天
    """
    if date is None:
        date = datetime.now().strftime("%Y-%m-%d")

    subject = f"每日简报 | AI+金融+政治 — {date}"

    body = f"""
    <html>
    <body>
        <h2>📰 每日简报 {date}</h2>
        <div>{content}</div>
        <hr>
        <p style="color:#888;font-size:12px;">
            此邮件由系统自动发送，请勿回复。<br>
            如需退订，请联系管理员。
        </p>
    </body>
    </html>
    """

    return EmailMessage(recipient_email=recipient, subject=subject, body=body)


# 测试用例
if __name__ == "__main__":
    import sys

    api_key = os.environ.get("SENDGRID_API_KEY")

    # MOCK_MODE 用于本地无 API Key 时验证代码逻辑
    mock_mode = os.environ.get("MOCK_MODE", "false").lower() == "true"

    if not api_key and not mock_mode:
        print("错误: 请设置环境变量 SENDGRID_API_KEY，或设置 MOCK_MODE=true 用于本地验证")
        sys.exit(1)

    if mock_mode:
        print("⚠️ MOCK_MODE: 模拟邮件发送，不调用真实 API")
        test_email = build_daily_brief_email(
            recipient="test@example.com",
            content="<p>这是测试简报内容。</p><ul><li>AI: 最新AI进展</li><li>金融: 市场动态</li><li>政治: 重要新闻</li></ul>"
        )
        test_email.sent_at = datetime.utcnow().isoformat() + "Z"
        test_email.status = "sent"
        print(f"模拟发送成功!")
        print(f"收件人: {test_email.recipient_email}")
        print(f"主题: {test_email.subject}")
        print(f"状态: {test_email.status}")
        print(f"发送时间: {test_email.sent_at}")
        sys.exit(0)

    sender = EmailSender(api_key)

    # 测试邮件
    test_email = build_daily_brief_email(
        recipient="test@example.com",
        content="<p>这是测试简报内容。</p><ul><li>AI: 最新AI进展</li><li>金融: 市场动态</li><li>政治: 重要新闻</li></ul>"
    )

    result = sender.send(test_email)
    print(f"发送状态: {test_email.status}")
    print(f"发送时间: {test_email.sent_at}")
    print(f"响应码: {result['status_code']}")