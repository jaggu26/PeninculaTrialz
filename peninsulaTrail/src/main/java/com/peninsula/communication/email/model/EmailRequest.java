package com.peninsula.communication.email.model;


public class EmailRequest {

    private String to;
    private String subject;
    private Body body;

    // Getters and setters

    public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public static class Body {
        private String greeting;
        private String main;
        private String footer;
		public String getGreeting() {
			return greeting;
		}
		public void setGreeting(String greeting) {
			this.greeting = greeting;
		}
		public String getMain() {
			return main;
		}
		public void setMain(String main) {
			this.main = main;
		}
		public String getFooter() {
			return footer;
		}
		public void setFooter(String footer) {
			this.footer = footer;
		}

        // Getters and setters
    }
}