# random_chat_java_stream_socket_multiclient
- Enter nickname
- ![image](https://user-images.githubusercontent.com/41092293/147593450-a68119d7-585d-49f1-9c0d-5a3580fd2ece.png)
- No user in queue for first user
- ![image](https://user-images.githubusercontent.com/41092293/147593458-8e5d3b2b-3421-49e8-b2ff-5a962bf0eaed.png)
- In queue screen
- ![image](https://user-images.githubusercontent.com/41092293/147593471-95a275cf-3365-4d35-a5df-f0a3e1015c8b.png)
- Found User in queue
- ![image](https://user-images.githubusercontent.com/41092293/147593567-27c7ef0a-4ae6-4466-b978-9c6b4dfa9378.png)
- User1 get alert
- ![image](https://user-images.githubusercontent.com/41092293/147593586-ed4bb540-6f09-443a-9bd6-10c2ca84b108.png)
- Chat (enter or click button)
- ![image](https://user-images.githubusercontent.com/41092293/147593633-dccc87f0-99df-4b5b-9c80-0153cbe65ecf.png)
- Leave chat anytime
- ![image](https://user-images.githubusercontent.com/41092293/147593672-4c8b258d-8243-4e9d-9fa4-47e1c2f5e285.png)
- Exit
- ![image](https://user-images.githubusercontent.com/41092293/147593697-c1f38056-a72a-4126-a1b6-2c7373499fa0.png)
- ![image](https://user-images.githubusercontent.com/41092293/147628277-780f6902-2428-4ce2-a381-9f4b3d032d77.png)
Người dùng A chọn một nickname bất kỳ để sử dụng ứng dụng (chỉ cần nickname, không cần
password hay các thông tin khác). Nếu nickname đang được một người dùng khác sử dụng, ứng
dụng sẽ thông báo lỗi để người dùng chọn một nickname khác.
Sau khi người dùng A chọn nickname thành công:
Nếu có 1 hoặc một số người dùng khác đang ở trạng thái chờ thi người dùng A và một
người ngẫu nhiên trong danh sách chờ sẽ được ghép đôi để chat. Người dùng A được phép
đồng hoặc từ chối ghép đôi:
Nếu đồng ỹ: hai bên bắt đầu phiên chat (dữ liệu chat giữa hai client chỉ có text,
không có file/image).
Nếu từ chối: hệ thống sẽ tim một người dùng khác trong danh sách chờ để ghép đôi
hoặc đẩy A vào danh sách chờ nếu A đă từ chối tất cả người dùng trong danh sách chờ.
Nếu không có người dùng nào khác đang ở trạng thái chờ thi A sẽ bị đẩy vào danh sách
chờ cho tới khi hệ thống tim được 1 người dùng để ghép đôi.
Tại bất kỳ thời điểm nào, một trong hai người dùng trong một phiên chat có thể thoát khỏi phiên
chat. Người dùng cỏn lại sẽ được hệ thống đẩy ra khỏi phiên chat và quay trở lại các hoạt động
tương tự lúc mới đăng nhập thành công.
Toàn bộ thông tin về người dùng (bao gồm nickname, lịch sử chat) sẽ được xóa hoàn toàn khi họ
thoát khỏi ứng dung.
