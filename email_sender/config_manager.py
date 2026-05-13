"""
邮件推送配置管理模块 - M3
支持配置文件管理订阅者、发送参数
"""

import os
import json
import configparser
from dataclasses import dataclass, asdict
from typing import List, Optional
from pathlib import Path

# 配置文件路径
CONFIG_DIR = Path(__file__).parent.parent
SUBSCRIBERS_FILE = CONFIG_DIR / "subscribers.json"
CONFIG_FILE = CONFIG_DIR / "email_config.ini"


# ---------------------------------------------------------------------------
# 订阅者管理
# ---------------------------------------------------------------------------

@dataclass
class Subscriber:
    """订阅者"""
    email: str
    name: str = ""
    active: bool = True

    @classmethod
    def from_dict(cls, data: dict) -> "Subscriber":
        return cls(
            email=data["email"],
            name=data.get("name", ""),
            active=data.get("active", True)
        )

    def to_dict(self) -> dict:
        return asdict(self)


def load_subscribers(path: Path = SUBSCRIBERS_FILE) -> List[Subscriber]:
    """从 JSON 文件加载订阅者列表"""
    if not path.exists():
        return []

    with open(path, "r", encoding="utf-8") as f:
        data = json.load(f)

    return [Subscriber.from_dict(s) for s in data.get("subscribers", [])]


def save_subscribers(subscribers: List[Subscriber], path: Path = SUBSCRIBERS_FILE) -> None:
    """保存订阅者列表到 JSON 文件"""
    with open(path, "w", encoding="utf-8") as f:
        json.dump({
            "subscribers": [s.to_dict() for s in subscribers]
        }, f, ensure_ascii=False, indent=2)


def add_subscriber(email: str, name: str = "", subscribers: List[Subscriber] = None) -> List[Subscriber]:
    """添加订阅者"""
    if subscribers is None:
        subscribers = load_subscribers()

    if any(s.email == email for s in subscribers):
        raise ValueError(f"订阅者 {email} 已存在")

    subscribers.append(Subscriber(email=email, name=name, active=True))
    save_subscribers(subscribers)
    return subscribers


def remove_subscriber(email: str, subscribers: List[Subscriber] = None) -> List[Subscriber]:
    """移除订阅者"""
    if subscribers is None:
        subscribers = load_subscribers()

    original_count = len(subscribers)
    subscribers = [s for s in subscribers if s.email != email]

    if len(subscribers) == original_count:
        raise ValueError(f"订阅者 {email} 不存在")

    save_subscribers(subscribers)
    return subscribers


def toggle_subscriber(email: str, active: bool, subscribers: List[Subscriber] = None) -> List[Subscriber]:
    """切换订阅者状态"""
    if subscribers is None:
        subscribers = load_subscribers()

    found = False
    for s in subscribers:
        if s.email == email:
            s.active = active
            found = True

    if not found:
        raise ValueError(f"订阅者 {email} 不存在")

    save_subscribers(subscribers)
    return subscribers


# ---------------------------------------------------------------------------
# 配置管理
# ---------------------------------------------------------------------------

@dataclass
class EmailConfig:
    """邮件配置"""
    from_email: str = "noreply@example.com"
    send_hour: int = 9   # UTC
    send_minute: int = 0
    email_enabled: bool = True
    mock_mode: bool = False

    @classmethod
    def from_file(cls, path: Path = CONFIG_FILE) -> "EmailConfig":
        if not path.exists():
            return cls()

        config = configparser.ConfigParser()
        config.read(path, encoding="utf-8")

        if "email" not in config:
            return cls()

        email_section = config["email"]
        return cls(
            from_email=email_section.get("from_email", "noreply@example.com"),
            send_hour=email_section.getint("send_hour", 9),
            send_minute=email_section.getint("send_minute", 0),
            email_enabled=email_section.getboolean("email_enabled", True),
            mock_mode=email_section.getboolean("mock_mode", False)
        )

    def to_file(self, path: Path = CONFIG_FILE) -> None:
        config = configparser.ConfigParser()
        config["email"] = {
            "from_email": self.from_email,
            "send_hour": str(self.send_hour),
            "send_minute": str(self.send_minute),
            "email_enabled": str(self.email_enabled).lower(),
            "mock_mode": str(self.mock_mode).lower()
        }

        Path(path).parent.mkdir(parents=True, exist_ok=True)
        with open(path, "w", encoding="utf-8") as f:
            config.write(f)


# ---------------------------------------------------------------------------
# CLI 入口
# ---------------------------------------------------------------------------

def main():
    import argparse

    parser = argparse.ArgumentParser(description="邮件推送配置管理")
    subparsers = parser.add_subparsers(dest="command", help="子命令")

    # 查看订阅者列表
    sub_list = subparsers.add_parser("list", help="查看订阅者列表")
    sub_list.add_argument("--json", action="store_true", help="JSON 格式输出")

    # 添加订阅者
    sub_add = subparsers.add_parser("add", help="添加订阅者")
    sub_add.add_argument("--email", required=True, help="邮箱地址")
    sub_add.add_argument("--name", default="", help="姓名（可选）")

    # 移除订阅者
    sub_remove = subparsers.add_parser("remove", help="移除订阅者")
    sub_remove.add_argument("--email", required=True, help="邮箱地址")

    # 切换订阅者状态
    sub_toggle = subparsers.add_parser("toggle", help="切换订阅者状态")
    sub_toggle.add_argument("--email", required=True, help="邮箱地址")
    sub_toggle.add_argument("--active", type=bool, help="True 激活，False 禁用")

    # 查看/修改配置
    sub_config = subparsers.add_parser("config", help="查看/修改配置")
    sub_config.add_argument("--from-email", help="发件人邮箱")
    sub_config.add_argument("--hour", type=int, help="发送小时（UTC）")
    sub_config.add_argument("--minute", type=int, help="发送分钟")
    sub_config.add_argument("--enable", type=bool, help="启用/禁用邮件推送")
    sub_config.add_argument("--show", action="store_true", help="显示当前配置")

    args = parser.parse_args()

    if args.command == "list":
        subscribers = load_subscribers()
        if args.json:
            print(json.dumps({"subscribers": [s.to_dict() for s in subscribers]}, ensure_ascii=False, indent=2))
        else:
            print(f"订阅者列表（共 {len(subscribers)} 人）：")
            for s in subscribers:
                status = "✓" if s.active else "✗"
                print(f"  [{status}] {s.email} ({s.name})")

    elif args.command == "add":
        subscribers = load_subscribers()
        subscribers = add_subscriber(args.email, args.name, subscribers)
        print(f"已添加订阅者：{args.email}")

    elif args.command == "remove":
        subscribers = load_subscribers()
        subscribers = remove_subscriber(args.email, subscribers)
        print(f"已移除订阅者：{args.email}")

    elif args.command == "toggle":
        subscribers = load_subscribers()
        subscribers = toggle_subscriber(args.email, args.active, subscribers)
        status = "激活" if args.active else "禁用"
        print(f"订阅者 {args.email} 已{status}")

    elif args.command == "config":
        cfg = EmailConfig.from_file()
        if args.show:
            print(f"当前配置：")
            print(f"  from_email: {cfg.from_email}")
            print(f"  send_time: {cfg.send_hour:02d}:{cfg.send_minute:02d} UTC")
            print(f"  email_enabled: {cfg.email_enabled}")
            print(f"  mock_mode: {cfg.mock_mode}")
            return

        if args.from_email:
            cfg.from_email = args.from_email
        if args.hour is not None:
            cfg.send_hour = args.hour
        if args.minute is not None:
            cfg.send_minute = args.minute
        if args.enable is not None:
            cfg.email_enabled = args.enable

        cfg.to_file()
        print("配置已保存")

    else:
        parser.print_help()


if __name__ == "__main__":
    main()