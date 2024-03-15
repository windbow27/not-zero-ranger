## Nói qua về activity và lưu trữ cho highscore  

Tui dùng RecyclerView -> cần Adapter class  
Lưu HighScore trong ArrayList<HighScore> cho dễ  
Trong internal storage, lưu ArrayList trên ở trong key 'highscoreList'  
### **Lưu ý**  
do highscore array lưu trong storage dưới dạng object, nên lúc lấy ra với đọc phải dùng thêm object stream
lúc thêm score mới vào, nhớ sort lại array 

Có chú thích về code trong HighScoreActivity, xem qua cho tui vui?  
Cũng thêm qua đống png với fonts... ehe

Ở setting activity chỉ cần gọi đến activity này là được, nút back tự thoát khỏi activity