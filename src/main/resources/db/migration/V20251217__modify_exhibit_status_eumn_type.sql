alter table exhibit
modify column status enum('FINISHED', 'ONGOING', 'UPCOMING', 'ENDING_SOON')not null ;