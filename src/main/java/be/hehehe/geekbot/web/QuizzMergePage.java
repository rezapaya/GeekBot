package be.hehehe.geekbot.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import be.hehehe.geekbot.persistence.dao.QuizzDAO;
import be.hehehe.geekbot.persistence.dao.QuizzMergeDAO;
import be.hehehe.geekbot.persistence.model.QuizzMergeException;
import be.hehehe.geekbot.persistence.model.QuizzMergeRequest;
import be.hehehe.geekbot.persistence.model.QuizzPlayer;
import be.hehehe.geekbot.utils.BundleService;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class QuizzMergePage extends TemplatePage {

	public QuizzMergePage() {
		add(new SubmitForm("form"));
		add(new MergeForm("merge-form"));
	}

	private class SubmitForm extends Form<List<QuizzPlayer>> {

		private String giver;
		private String receiver;

		FeedbackPanel messages = new FeedbackPanel("messages");

		public SubmitForm(String id) {
			super(id);
			messages.setVisible(false);

			IModel<List<String>> model = new LoadableDetachableModel<List<String>>() {
				@Override
				protected List<String> load() {
					return Lists.transform(getBean(QuizzDAO.class)
							.getPlayersOrderByName(),
							new Function<QuizzPlayer, String>() {
								@Override
								public String apply(QuizzPlayer input) {
									return input.getName();
								}
							});
				}
			};

			add(new Button("submit-button"));
			DropDownChoice<String> giverChoice = new DropDownChoice<String>(
					"giver", new PropertyModel<String>(this, "giver"), model);
			add(giverChoice);

			DropDownChoice<String> receiverChoice = new DropDownChoice<String>(
					"receiver", new PropertyModel<String>(this, "receiver"),
					model);
			add(receiverChoice);
			add(messages);

		}

		@Override
		protected void onSubmit() {
			try {
				getBean(QuizzMergeDAO.class).add(receiver, giver);
				setResponsePage(QuizzMergePage.class);
			} catch (QuizzMergeException e) {
				messages.setVisible(true);
				error(e.getMessage());
			}
		}

	}

	private class MergeForm extends Form<List<QuizzMergeRequest>> {

		private String password;

		public MergeForm(String id) {
			super(id);

			IModel<List<QuizzMergeRequest>> model = new LoadableDetachableModel<List<QuizzMergeRequest>>() {
				@Override
				protected List<QuizzMergeRequest> load() {
					return getBean(QuizzMergeDAO.class).findAll();
				}
			};

			add(new PasswordTextField("password", new PropertyModel<String>(
					this, "password")));
			ListView<QuizzMergeRequest> requestsView = new PropertyListView<QuizzMergeRequest>(
					"requests", model) {
				@Override
				protected void populateItem(ListItem<QuizzMergeRequest> item) {
					QuizzMergeRequest request = item.getModelObject();
					final Long requestId = request.getId();
					item.add(new Label("receiving", "receiver"));
					item.add(new Label("giving", "giver"));
					item.add(new SubmitLink("accept") {
						@Override
						public void onSubmit() {
							System.out.println(password);
							System.out.println(getBean(BundleService.class)
									.getAdminPassword());
							if (StringUtils.equals(password,
									getBean(BundleService.class)
											.getAdminPassword())) {
								getBean(QuizzMergeDAO.class).executeMerge(
										requestId);
							}
							setResponsePage(QuizzMergePage.class);
						}
					});
					item.add(new SubmitLink("deny") {
						@Override
						public void onSubmit() {
							if (StringUtils.equals(password,
									getBean(BundleService.class)
											.getAdminPassword())) {
								getBean(QuizzMergeDAO.class).deleteById(
										requestId);
							}
							setResponsePage(QuizzMergePage.class);
						}
					});
				}
			};
			requestsView.setReuseItems(true);
			add(requestsView);
		}
	}

	@Override
	protected String getTitle() {
		return "Quizz Merge Requests";
	}

}
