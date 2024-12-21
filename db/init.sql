-- テーブル作成
create table feed (
  id serial primary key,
  url text not null,
  title text not null,
  description text not null,
);

create table article (
  id serial primary key,
  feed_id integer not null references feed(id),
  title text not null,
  url text not null,
  content text not null,
  author text not null,
  published_at timestamp not null,
);


-- テストデータ挿入
insert into feed (url, title, description) values
('https://www.google.com', 'Google', 'Googleのトップページです。'),
('https://www.yahoo.co.jp', 'Yahoo! JAPAN', 'Yahoo! JAPANのトップページです。');
