FROM php:8.2-apache

RUN docker-php-ext-install pdo pdo_mysql mysqli
RUN a2enmod rewrite

# On change la racine d'Apache vers le dossier public
ENV APACHE_DOCUMENT_ROOT /var/www/html/public
RUN sed -ri -e 's!/var/www/html!${APACHE_DOCUMENT_ROOT}!g' /etc/apache2/sites-available/*.conf
RUN sed -ri -e 's!/var/www/html!${APACHE_DOCUMENT_ROOT}!g' /etc/apache2/apache2.conf /etc/apache2/conf-available/*.conf

WORKDIR /var/www/html

# On copie UNIQUEMENT le contenu de toyboxing-app vers /var/www/html
COPY toyboxing-app/ .

RUN chown -R www-data:www-data /var/www/html
