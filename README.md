# 邮件推送功能 - M1+M2

SendGrid SDK 集成与定时自动化触发

## M1 环境配置

```bash
export SENDGRID_API_KEY="your_sendgrid_api_key"
export FROM_EMAIL="noreply@example.com"
pip install -r requirements.txt
```

## M2 定时任务

```bash
# 手动触发一次发送
python email_sender/scheduler.py --once

# 后台定时运行（每日 UTC 9:00）
python email_sender/scheduler.py --daemon

# 自定义发送时间
python email_sender/scheduler.py --daemon --hour 10 --minute 30
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
# 无 API Key 本地验证
MOCK_MODE=true python email_sender/sender.py
MOCK_MODE=true python email_sender/scheduler.py --once
```