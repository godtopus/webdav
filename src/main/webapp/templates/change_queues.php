<?php
	class ChangeQueues {
		private static $user1 = new SplQueue();
		private static $user2 = new SplQueue();

		public static function get_user1() {
			while ($user1 -> isEmpty()) {
			}
			
			echo self::$user1 -> dequeue();
		}
		
		public static function post_user1($data) {
			$user2 -> enqueue($data);
		}

		public static function get_user2() {
			while ($user2 -> isEmpty()) {
			}
			
			echo self::$user2 -> dequeue();
		}
		
		public static function post_user2($data) {
			$user1 -> enqueue($data);
		}
	}
?>