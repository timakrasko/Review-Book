<#-- Шаблон FreeMarker для повідомлення про нову книгу -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Нова книга!</title>
    <style>
        body { font-family: Arial, sans-serif; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9; }
        .header { text-align: center; border-bottom: 2px solid #007bff; padding-bottom: 10px; margin-bottom: 20px; }
        .book-info { background: #fff; padding: 15px; border-radius: 4px; border-left: 5px solid #007bff; }
        .label { font-weight: bold; color: #555; }
        .footer { margin-top: 30px; font-size: 12px; color: #888; text-align: center; border-top: 1px solid #eee; padding-top: 10px; }
        .rarity { color: #d9534f; font-weight: bold; margin-top: 10px; border: 1px dashed #d9534f; padding: 5px; display: inline-block; }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <img src="https://raw.githubusercontent.com/spring-projects/spring-framework/main/framework-docs/src/docs/antora/modules/ROOT/assets/images/spring-logo.svg"
             style="width:150px;" alt="Library App Logo">
        <h2>Додано нову книгу</h2>
    </div>

    <p>Вітаємо! У наш каталог було додано нову книгу:</p>

    <div class="book-info">
        <p><span class="label">Назва:</span> ${title}</p>
        <p><span class="label">Автор:</span> ${author}</p>
        <p><span class="label">Рік видання:</span> ${year?c}</p> <#-- ?c використовується для виводу числа без форматування (напр. 2023, а не 2,023) -->

        <#-- Умовний блок для раритетних книг (до 2000 року) -->
        <#if year < 2000>
            <div class="rarity">🔥 Увага! Це раритетне видання!</div>
        </#if>
    </div>

    <p>Дата додавання: <b>${added}</b></p>

    <div class="footer">
        <p>BookApp Library System © 2025</p>
        <p>Цей лист згенеровано автоматично, не відповідайте на нього.</p>
    </div>
</div>
</body>
</html>