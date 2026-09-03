update books
set series_index = null
where series_id is null;

alter table books
    add constraint ck_book_series_index_requires_series
        check (
            series_index is null
                or series_id is not null
            );