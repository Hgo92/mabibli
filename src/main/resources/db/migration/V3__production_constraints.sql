create unique index uk_users_username_lower
    on users (lower(username));

create unique index uk_libraries_user_title_lower
    on libraries (user_id, lower(title));

create unique index uk_series_user_title_lower
    on series (user_id, lower(title));

create index idx_book_series
    on books (series_id);

alter table books
    add constraint ck_book_status_valid
        check (
            status in (
                       'A_LIRE',
                       'EN_COURS',
                       'LU',
                       'ABANDONNE'
                )
            );

alter table book_category
    add constraint ck_book_category_valid
        check (
            category in (
                         'ROMAN',
                         'NOUVELLE',
                         'POESIE',
                         'FANTASY',
                         'POLICIER',
                         'AVENTURE',
                         'THRILLER',
                         'HORREUR',
                         'EROTIQUE',
                         'ROMANCE',
                         'HISTORIQUE',
                         'BIOGRAPHIE',
                         'SCIENCE_FICTION',
                         'BD',
                         'MANGA',
                         'HUMOUR',
                         'PHILOSOPHIE',
                         'ESSAI',
                         'THEATRE',
                         'DRAME',
                         'AUTRE'
                )
            );