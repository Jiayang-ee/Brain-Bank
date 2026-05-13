# 邮件推送功能 - M1

SendGrid SDK 集成与单封邮件发送验证

## 环境配置

```bash
export SENDGRID_API_KEY="your_sendgrid_api_key"
export FROM_EMAIL="noreply@example.com"
pip install -r requirements.txt
```

## 使用方法

```python
from email_sender.sender import EmailSender, build_daily_brief_email

sender = EmailSender()

email = build_daily_brief_email(
    recipient="user@example.com",
    content="<p>简报内容...</p>"
)

result = sender.send(email)
print(email.status)  # sent / failed
```

## 测试

```bash
python email_sender/sender.py
```