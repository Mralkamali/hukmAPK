# حسابة الورقة — Android

نسخة Android (Kotlin + Jetpack Compose) من تطبيق **حسابة الورقة**، منقولة طبق الأصل من نسخة iOS/SwiftUI:
كوت (حكم متحرك)، حكم ثابت، بلوت (كبوت/مشاريع)، طرنيب، ولعبة مخصصة (٢–٤ فرق) — مع سجل الجولات،
عدّاد الكاسات، تراجع، حفظ تلقائي لكل لعبة، مظهر غامق/فاتح/تلقائي، وصوت التسجيل.

- `applicationId`: `com.mohammedalkamali.hesabatalwaraqa`
- `minSdk` 24 · `targetSdk` 34 · `versionName` 1.4
- بدون إعلانات

## كيف تطلّع ملف APK (GitHub Actions)

1. أنشئ مستودع جديد على GitHub (خاص أو عام)، بدون README.
2. من مجلد المشروع نفّذ:

```bash
cd "hukm-android"
git init
git add .
git commit -m "حسابة الورقة — Android port"
git branch -M main
git remote add origin https://github.com/<USERNAME>/<REPO>.git
git push -u origin main
```

3. افتح تبويب **Actions** في المستودع — سيبدأ workflow باسم *Build APK* تلقائياً.
4. بعد ٣–٦ دقائق:
   - نزّل الـ APK من **Actions ▸ آخر تشغيل ▸ Artifacts ▸ `hesabat-alwaraqa-apk`**، أو
   - من تبويب **Releases** ستجد إصداراً جديداً (`build-N`) مرفق فيه ملف `hesabat-alwaraqa-N.apk`.

الـ APK ناتج بصيغة **debug** (موقّع بمفتاح debug) — يتثبّت مباشرة على أي جهاز Android بعد تفعيل
«تثبيت تطبيقات من مصادر غير معروفة». للنشر على Google Play لاحقاً يلزم بناء `release` موقّع بمفتاحك.

## البناء محلياً (اختياري)

يحتاج JDK 17 + Android SDK:

```bash
./gradlew :app:assembleDebug
# الناتج: app/build/outputs/apk/debug/app-debug.apk
```
