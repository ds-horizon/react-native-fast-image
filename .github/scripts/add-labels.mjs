import { Octokit } from "@octokit/rest";

async function isFirstIssue(
    octakit,
    owner,
    repo,
    sender,
    curIssueNumber
) {
  for await (const {data: issues} of octakit.paginate.iterator(octakit.rest.issues.listForRepo, {
    owner: owner,
    repo: repo,
    creator: sender,
    state: 'all',
    per_page: 100
  })) {
    for (const issue of issues) {
      if (issue.number < curIssueNumber && !issue.pull_request) {
        return false;
      }
    }
  }

  return true;
}

async function isFirstPull(
    octokit,
    owner,
    repo,
    sender,
    curPullNumber,
    page
) {
  const {status, data: pulls} = await octokit.rest.pulls.list({
    owner: owner,
    repo: repo,
    per_page: 100,
    page: page,
    state: 'all'
  });

  if (status !== 200) {
    throw new Error(`Received unexpected API status code ${status}`);
  }

  if (pulls.length === 0) {
    return true;
  }

  for (const pull of pulls) {
    const login = pull.user?.login;
    if (login === sender && pull.number < curPullNumber) {
      return false;
    }
  }

  return await isFirstPull(
      octokit,
      owner,
      repo,
      sender,
      curPullNumber,
      page + 1
  );
}
async function addLabels(octokit, owner, repo, issueNumber, labels) {
  try {
    await octokit.rest.issues.addLabels({
      owner,
      repo,
      issue_number: issueNumber,
      labels,
    });
    console.log(`Successfully added labels [${labels}] to issue/PR #${issueNumber}`);
  } catch (error) {
    console.error(`Failed to add labels: ${error.message}`);
    process.exit(1);
  }
}

async function run() {
  const token = process.env.GITHUB_TOKEN;
  const octokit = new Octokit({
    auth: token,
  });
  const [owner, repo] = process.env.GITHUB_REPOSITORY.split("/");
  const eventType = process.env.GITHUB_EVENT_NAME;
  const githubprnumber = process.env.GITHUB_PR_NUMBER;
  const githubissuenumber = process.env.GITHUB_ISSUE_NUMBER;
  const username = process.env.GITHUB_ACTOR; // The actor is the user who triggered the event

  let issueNumber, isFirstContribution = false;
  if (eventType === "issues") {
    issueNumber = githubissuenumber
    isFirstContribution = await isFirstIssue(octokit, owner, repo, username,issueNumber);

  } else if (eventType === "pull_request" || eventType === "pull_request_target") {
    issueNumber = githubprnumber
    isFirstContribution = await isFirstPull(octokit, owner, repo, username,issueNumber,1);

  } else {
    console.error("Unsupported event type:", eventType);
    process.exit(1);
  }

  const labelsForIssue = process.env.LABELS_FOR_ISSUE ? process.env.LABELS_FOR_ISSUE.split(",") : ["First Issue"];
  const labelsForPR = process.env.LABELS_FOR_PR ? process.env.LABELS_FOR_PR.split(",") : ["First PR"];
  const labels = eventType === "issues" ? labelsForIssue : labelsForPR;

  if(!issueNumber) {
    console.error("Not valid Issue");
    process.exit(1);
  }
  if(!isFirstContribution) {
    console.error("Not First Contribution");
    return;
  }
  // Add the labels
  await addLabels(octokit, owner, repo, issueNumber, labels);
}

run();
