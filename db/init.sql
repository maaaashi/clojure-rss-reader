-- テーブル作成
create table feed (
  id serial primary key,
  url text not null,
  title text not null,
  description text not null
);

create table article (
  id serial primary key,
  feed_id integer not null references feed(id),
  title text not null,
  url text not null,
  content text,
  author text,
  published_at text
);
